package com.omo.backend.domain.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omo.backend.domain.auth.converter.OAuthConverter;
import com.omo.backend.domain.auth.dto.AuthResponseDTO;
import com.omo.backend.domain.auth.dto.OAuthRequestDTO;
import com.omo.backend.domain.auth.dto.OAuthResponseDTO;
import com.omo.backend.domain.auth.dto.OAuthStateDTO;
import com.omo.backend.domain.auth.enums.OAuthPurpose;
import com.omo.backend.domain.auth.exception.AuthErrorCode;
import com.omo.backend.domain.auth.exception.AuthException;
import com.omo.backend.domain.member.converter.MemberConverter;
import com.omo.backend.domain.member.entity.Member;
import com.omo.backend.domain.member.entity.SocialAccount;
import com.omo.backend.domain.member.enums.MemberProvider;
import com.omo.backend.domain.member.enums.MemberStatus;
import com.omo.backend.domain.member.exception.MemberErrorCode;
import com.omo.backend.domain.member.exception.MemberException;
import com.omo.backend.domain.member.repository.MemberRepository;
import com.omo.backend.domain.member.repository.MemberSettingsRepository;
import com.omo.backend.domain.member.repository.SocialAccountRepository;
import com.omo.backend.domain.member.service.TermsAgreementService;
import com.omo.backend.domain.terms.entity.Terms;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoogleOAuthService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String GOOGLE_AUTHORIZATION_URI = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String GOOGLE_TOKEN_URI = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_USER_INFO_URI = "https://openidconnect.googleapis.com/v1/userinfo";
    private static final String OAUTH_STATE_KEY_PREFIX = "OAUTH:GOOGLE:STATE:";
    private static final String LOGIN_TICKET_KEY_PREFIX = "OAUTH:GOOGLE:LOGIN:";
    private static final Duration OAUTH_STATE_EXPIRATION = Duration.ofMinutes(5);
    private static final Duration LOGIN_TICKET_EXPIRATION = Duration.ofMinutes(3);

    private final SocialAccountRepository socialAccountRepository;
    private final MemberRepository memberRepository;
    private final MemberSettingsRepository memberSettingsRepository;
    private final TermsAgreementService termsAgreementService;
    private final GoogleProfileImageService googleProfileImageService;
    private final AuthCommandService authCommandService;
    private final StringRedisTemplate redisTemplate;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @Value("${oauth.frontend-redirect-uri}")
    private String frontendRedirectUri;

    // 필수 약관을 검증하고 회원가입용 state를 Redis에 저장한 뒤 Google 인증 URL 생성
    @Transactional(readOnly = true)
    public OAuthResponseDTO.GoogleAuthorizationUrlDTO createSignupAuthorizationUrl(OAuthRequestDTO.GoogleSignupStartDTO request) {
        termsAgreementService.validateAndGetAgreedTerms(request.agreedTermsIds());

        String state = UUID.randomUUID().toString();
        OAuthStateDTO.GoogleOAuthStateDTO oauthState = new OAuthStateDTO.GoogleOAuthStateDTO(OAuthPurpose.SIGNUP, List.copyOf(request.agreedTermsIds()));
        saveOAuthState(state, oauthState);

        return new OAuthResponseDTO.GoogleAuthorizationUrlDTO(createGoogleAuthorizationUrl(state));
    }

    // 로그인용 state를 Redis에 저장한 뒤 Google 인증 URL 생성
    public OAuthResponseDTO.GoogleAuthorizationUrlDTO createLoginAuthorizationUrl() {
        String state = UUID.randomUUID().toString();
        OAuthStateDTO.GoogleOAuthStateDTO oauthState = new OAuthStateDTO.GoogleOAuthStateDTO(OAuthPurpose.LOGIN, List.of());
        saveOAuthState(state, oauthState);

        return new OAuthResponseDTO.GoogleAuthorizationUrlDTO(createGoogleAuthorizationUrl(state));
    }

    private String createGoogleAuthorizationUrl(String state) {
        return UriComponentsBuilder.fromUriString(GOOGLE_AUTHORIZATION_URI)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "openid email profile")
                .queryParam("state", state)
                .build()
                .encode()
                .toUriString();
    }

    // Google 콜백의 요청 목적에 따라 신규 회원가입 또는 기존 회원 로그인 처리
    @Transactional
    public String handleCallback(String code, String state, String authorizationError) {
        // Google 인증 성공 여부와 요청 시 발급한 state를 검증
        validateAuthorizationResponse(code, state, authorizationError);
        OAuthStateDTO.GoogleOAuthStateDTO oauthState = consumeOAuthState(state);

        // 인가 코드를 Google 액세스 토큰으로 교환한 뒤 사용자 정보를 조회
        String googleAccessToken = requestGoogleAccessToken(code);
        OAuthResponseDTO.GoogleUserInfoDTO userInfo = requestGoogleUserInfo(googleAccessToken);
        validateGoogleUserInfo(userInfo);

        Member member = switch (oauthState.purpose()) {
            case SIGNUP -> createGoogleMember(userInfo, oauthState.agreedTermsIds());
            case LOGIN -> getGoogleMember(userInfo);
        };
        validateActiveMember(member);

        String ticket = createLoginTicket(member);
        return UriComponentsBuilder.fromUriString(frontendRedirectUri)
                .queryParam("ticket", ticket)
                .build()
                .encode()
                .toUriString();
    }

    // 일회용 로그인 티켓을 소비하고 기존 JWT 발급 로직을 호출
    @Transactional(readOnly = true)
    public AuthResponseDTO.LoginResultDTO exchangeLoginTicket(OAuthRequestDTO.GoogleLoginExchangeDTO request) {
        String memberIdValue = redisTemplate.opsForValue().getAndDelete(loginTicketKey(request.ticket()));
        if (!StringUtils.hasText(memberIdValue)) {
            throw new AuthException(AuthErrorCode.OAUTH_TICKET_INVALID);
        }

        Member member;
        try {
            member = memberRepository.findById(Long.valueOf(memberIdValue))
                    .orElseThrow(() -> new AuthException(AuthErrorCode.OAUTH_TICKET_INVALID));
        } catch (NumberFormatException exception) {
            throw new AuthException(AuthErrorCode.OAUTH_TICKET_INVALID);
        }

        validateActiveMember(member);
        return authCommandService.issueLoginTokens(member);
    }

    // Google 콜백에서 받은 인가 코드를 Google 액세스 토큰으로 교환
    private String requestGoogleAccessToken(String code) {
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("code", code);
        request.add("client_id", clientId);
        request.add("client_secret", clientSecret);
        request.add("redirect_uri", redirectUri);
        request.add("grant_type", "authorization_code");

        try {
            OAuthResponseDTO.GoogleTokenDTO response = RestClient.create()
                    .post()
                    .uri(GOOGLE_TOKEN_URI)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(request)
                    .retrieve()
                    .body(OAuthResponseDTO.GoogleTokenDTO.class);

            if (response == null || !StringUtils.hasText(response.accessToken())) {
                throw new AuthException(AuthErrorCode.GOOGLE_TOKEN_REQUEST_FAILED);
            }
            return response.accessToken();
        } catch (RestClientResponseException e) {
            throw new AuthException(AuthErrorCode.GOOGLE_TOKEN_REQUEST_FAILED);
        }
    }

    // Google 액세스 토큰을 사용해 사용자의 고유 ID, 이메일, 이름 등을 조회
    private OAuthResponseDTO.GoogleUserInfoDTO requestGoogleUserInfo(String accessToken) {
        try {
            OAuthResponseDTO.GoogleUserInfoDTO response = RestClient.create()
                    .get()
                    .uri(GOOGLE_USER_INFO_URI)
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .body(OAuthResponseDTO.GoogleUserInfoDTO.class);

            if (response == null) {
                throw new AuthException(AuthErrorCode.GOOGLE_USER_INFO_REQUEST_FAILED);
            }
            return response;
        } catch (RestClientResponseException e) {
            throw new AuthException(AuthErrorCode.GOOGLE_USER_INFO_REQUEST_FAILED);
        }
    }

    // 약관을 재검증하고 신규 Google 회원과 가입 관련 데이터 생성
    private Member createGoogleMember(OAuthResponseDTO.GoogleUserInfoDTO userInfo, List<Long> agreedTermsIds) {
        if (socialAccountRepository.findByProviderAndProviderUserId(MemberProvider.GOOGLE, userInfo.sub()).isPresent()) {
            throw new AuthException(AuthErrorCode.OAUTH_ACCOUNT_ALREADY_EXISTS);
        }
        if (memberRepository.existsByEmail(userInfo.email())) {
            throw new AuthException(AuthErrorCode.OAUTH_ACCOUNT_LINK_REQUIRED);
        }

        List<Terms> agreedTerms = termsAgreementService.validateAndGetAgreedTerms(agreedTermsIds);

        // 회원, 기본 설정, 약관 동의, 소셜 계정 연동 정보를 하나의 트랜잭션으로 저장
        Member member = memberRepository.save(OAuthConverter.toGoogleMember(userInfo));
        memberSettingsRepository.save(MemberConverter.toDefaultMemberSettings(member));
        termsAgreementService.saveMemberTerms(member, agreedTerms);
        socialAccountRepository.save(OAuthConverter.toGoogleSocialAccount(member, userInfo));

        String profileImageKey = googleProfileImageService.upload(member.getId(), userInfo.picture());
        if (profileImageKey != null) {
            member.updateProfileImage(profileImageKey);
        }

        return member;
    }

    // Google sub와 연결된 회원을 조회하고, 동일 이메일 회원이 있으면 계정 연동 안내
    private Member getGoogleMember(OAuthResponseDTO.GoogleUserInfoDTO userInfo) {
        SocialAccount socialAccount = socialAccountRepository.findByProviderAndProviderUserId(MemberProvider.GOOGLE, userInfo.sub()).orElse(null);

        if (socialAccount != null) {
            return socialAccount.getMember();
        }

        if (memberRepository.existsByEmail(userInfo.email())) {
            throw new AuthException(AuthErrorCode.OAUTH_ACCOUNT_LINK_REQUIRED);
        }
        throw new AuthException(AuthErrorCode.OAUTH_SIGNUP_REQUIRED);
    }

    // OAuth 요청 목적과 약관 정보를 JSON으로 변환해 state와 함께 Redis에 저장
    private void saveOAuthState(String state, OAuthStateDTO.GoogleOAuthStateDTO oauthState) {
        try {
            redisTemplate.opsForValue().set(stateKey(state), OBJECT_MAPPER.writeValueAsString(oauthState), OAUTH_STATE_EXPIRATION);
        } catch (JsonProcessingException exception) {
            throw new AuthException(AuthErrorCode.OAUTH_STATE_INVALID);
        }
    }

    // 회원 ID를 3분 동안 Redis에 저장하고 프론트에 전달할 일회용 티켓 생성
    private String createLoginTicket(Member member) {
        String ticket = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(loginTicketKey(ticket), member.getId().toString(), LOGIN_TICKET_EXPIRATION);
        return ticket;
    }

    // Redis의 state를 조회와 동시에 삭제하여 만료·위조·재사용 요청을 차단
    private OAuthStateDTO.GoogleOAuthStateDTO consumeOAuthState(String state) {
        String savedState = redisTemplate.opsForValue().getAndDelete(stateKey(state));
        if (!StringUtils.hasText(savedState)) {
            throw new AuthException(AuthErrorCode.OAUTH_STATE_INVALID);
        }

        try {
            return OBJECT_MAPPER.readValue(savedState, OAuthStateDTO.GoogleOAuthStateDTO.class);
        } catch (JsonProcessingException exception) {
            throw new AuthException(AuthErrorCode.OAUTH_STATE_INVALID);
        }
    }

    // 사용자가 Google 인증을 취소했거나 필수 콜백 값이 없는지 확인
    private void validateAuthorizationResponse(String code, String state, String authorizationError) {
        if (StringUtils.hasText(authorizationError) || !StringUtils.hasText(code)) {
            throw new AuthException(AuthErrorCode.OAUTH_AUTHORIZATION_FAILED);
        }
        if (!StringUtils.hasText(state)) {
            throw new AuthException(AuthErrorCode.OAUTH_STATE_INVALID);
        }
    }

    // 회원 식별에 필요한 Google 고유 ID와 인증된 이메일이 존재하는지 확인
    private void validateGoogleUserInfo(OAuthResponseDTO.GoogleUserInfoDTO userInfo) {
        if (!StringUtils.hasText(userInfo.sub()) || !StringUtils.hasText(userInfo.email()) || !Boolean.TRUE.equals(userInfo.emailVerified())) {
            throw new AuthException(AuthErrorCode.GOOGLE_EMAIL_NOT_VERIFIED);
        }
    }

    // 탈퇴 또는 비활성 회원의 Google 로그인을 차단
    private void validateActiveMember(Member member) {
        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
        }
    }

    private String stateKey(String state) {
        return OAUTH_STATE_KEY_PREFIX + state;
    }

    private String loginTicketKey(String ticket) {
        return LOGIN_TICKET_KEY_PREFIX + ticket;
    }
}
