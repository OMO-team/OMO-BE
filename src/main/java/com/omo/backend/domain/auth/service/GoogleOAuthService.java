package com.omo.backend.domain.auth.service;

import com.omo.backend.domain.auth.converter.OAuthConverter;
import com.omo.backend.domain.auth.dto.AuthResponseDTO;
import com.omo.backend.domain.auth.dto.OAuthResponseDTO;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoogleOAuthService {

    private static final String GOOGLE_AUTHORIZATION_URI = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String GOOGLE_TOKEN_URI = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_USER_INFO_URI = "https://openidconnect.googleapis.com/v1/userinfo";
    private static final String OAUTH_STATE_KEY_PREFIX = "OAUTH:GOOGLE:STATE:";
    private static final Duration OAUTH_STATE_EXPIRATION = Duration.ofMinutes(5);

    private final SocialAccountRepository socialAccountRepository;
    private final MemberRepository memberRepository;
    private final MemberSettingsRepository memberSettingsRepository;
    private final GoogleProfileImageService googleProfileImageService;
    private final AuthCommandService authCommandService;
    private final StringRedisTemplate redisTemplate;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    // OAuth 요청 위조 방지를 위한 state를 Redis에 저장하고 Google 인증 URL을 생성
    public String createAuthorizationUrl() {
        String state = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(stateKey(state), "login", OAUTH_STATE_EXPIRATION);

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

    // Google 콜백을 검증하고 Google 사용자에 대응하는 OMO 로그인 토큰을 발급
    @Transactional
    public AuthResponseDTO.LoginResultDTO login(String code, String state, String authorizationError) {
        // Google 인증 성공 여부와 요청 시 발급한 state를 검증
        validateAuthorizationResponse(code, state, authorizationError);
        validateAndConsumeState(state);

        // 인가 코드를 Google 액세스 토큰으로 교환한 뒤 사용자 정보를 조회
        String googleAccessToken = requestGoogleAccessToken(code);
        OAuthResponseDTO.GoogleUserInfoDTO userInfo = requestGoogleUserInfo(googleAccessToken);
        validateGoogleUserInfo(userInfo);

        // 소셜 계정에 연결된 회원을 조회하거나 신규 Google 회원을 생성
        Member member = findOrCreateMember(userInfo);
        validateActiveMember(member);

        // 기존 일반 로그인과 동일한 OMO 액세스 토큰 및 리프레시 토큰 발급
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

    // Google의 고유 사용자 ID(sub)로 연결된 소셜 계정을 조회
    private Member findOrCreateMember(OAuthResponseDTO.GoogleUserInfoDTO userInfo) {
        return socialAccountRepository
                .findByProviderAndProviderUserId(MemberProvider.GOOGLE, userInfo.sub())
                .map(SocialAccount::getMember)
                .orElseGet(() -> createGoogleMember(userInfo));
    }

    // 동일 이메일의 기존 회원은 자동 연동하지 않고 신규 Google 회원만 생성
    private Member createGoogleMember(OAuthResponseDTO.GoogleUserInfoDTO userInfo) {
        if (memberRepository.existsByEmail(userInfo.email())) {
            throw new AuthException(AuthErrorCode.OAUTH_ACCOUNT_LINK_REQUIRED);
        }

        // 회원, 기본 회원 설정, 소셜 계정 연동 정보를 하나의 트랜잭션으로 저장
        Member member = memberRepository.save(OAuthConverter.toGoogleMember(userInfo));
        memberSettingsRepository.save(MemberConverter.toDefaultMemberSettings(member));
        socialAccountRepository.save(OAuthConverter.toGoogleSocialAccount(member, userInfo));

        String profileImageKey = googleProfileImageService.upload(member.getId(), userInfo.picture());
        if (profileImageKey != null) {
            member.updateProfileImage(profileImageKey);
        }

        return member;
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

    // Redis의 state를 조회와 동시에 삭제하여 만료·위조·재사용 요청을 차단
    private void validateAndConsumeState(String state) {
        String savedState = redisTemplate.opsForValue().getAndDelete(stateKey(state));
        if (!StringUtils.hasText(savedState)) {
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
}
