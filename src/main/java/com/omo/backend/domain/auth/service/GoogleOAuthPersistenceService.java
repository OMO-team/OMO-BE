package com.omo.backend.domain.auth.service;

import com.omo.backend.domain.auth.converter.OAuthConverter;
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
import com.omo.backend.domain.member.service.TermsAgreementService;
import com.omo.backend.domain.terms.entity.Terms;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoogleOAuthPersistenceService {

    private final MemberRepository memberRepository;
    private final MemberSettingsRepository memberSettingsRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final TermsAgreementService termsAgreementService;

    // 신규 Google 회원과 기본 설정, 약관 동의, 소셜 계정을 하나의 트랜잭션으로 저장
    @Transactional
    public Member createGoogleMember(OAuthResponseDTO.GoogleUserInfoDTO userInfo, List<Long> agreedTermsIds) {
        if (socialAccountRepository.findByProviderAndProviderUserId(MemberProvider.GOOGLE, userInfo.sub()).isPresent()) {
            throw new AuthException(AuthErrorCode.OAUTH_ACCOUNT_ALREADY_EXISTS);
        }
        if (memberRepository.existsByEmail(userInfo.email())) {
            throw new AuthException(AuthErrorCode.OAUTH_ACCOUNT_LINK_REQUIRED);
        }

        try {
            List<Terms> agreedTerms = termsAgreementService.validateAndGetAgreedTerms(agreedTermsIds);

            // 회원, 기본 설정, 약관 동의, 소셜 계정 연동 정보를 하나의 트랜잭션으로 저장
            Member member = memberRepository.save(OAuthConverter.toGoogleMember(userInfo));
            memberSettingsRepository.save(MemberConverter.toDefaultMemberSettings(member));
            termsAgreementService.saveMemberTerms(member, agreedTerms);
            socialAccountRepository.saveAndFlush(OAuthConverter.toGoogleSocialAccount(member, userInfo));
            return member;
        } catch (DataIntegrityViolationException exception) {
            // 동시 회원가입 중 DB 유니크 제약을 먼저 선점한 요청이 있으면 기존 회원 안내
            throw new AuthException(AuthErrorCode.OAUTH_ACCOUNT_ALREADY_EXISTS);
        }
    }

    // S3 업로드가 완료된 Google 프로필 이미지 Object Key 반영
    @Transactional
    public void updateProfileImage(Long memberId, String profileImageKey) {
        Member member = getActiveMember(memberId);
        member.updateProfileImage(profileImageKey);
    }

    // Google 계정과 연결된 활성 회원을 조회하고 미가입·미연동 상태를 구분
    @Transactional(readOnly = true)
    public Member getGoogleMember(OAuthResponseDTO.GoogleUserInfoDTO userInfo) {
        SocialAccount socialAccount = socialAccountRepository.findByProviderAndProviderUserId(MemberProvider.GOOGLE, userInfo.sub()).orElse(null);

        if (socialAccount != null) {
            Member member = socialAccount.getMember();
            validateActiveMember(member);
            return member;
        }

        if (memberRepository.existsByEmail(userInfo.email())) {
            throw new AuthException(AuthErrorCode.OAUTH_ACCOUNT_LINK_REQUIRED);
        }
        throw new AuthException(AuthErrorCode.OAUTH_SIGNUP_REQUIRED);
    }

    // 로그인 회원과 Google 계정의 중복 연결을 검증하고 소셜 계정 저장
    @Transactional
    public Member linkGoogleAccount(Long memberId, OAuthResponseDTO.GoogleUserInfoDTO userInfo) {
        // 동일 회원의 연결 요청을 직렬화하여 member_id/provider 중복 저장 경쟁을 방지
        Member member = getActiveMemberForUpdate(memberId);

        if (socialAccountRepository.existsByMemberIdAndProvider(member.getId(), MemberProvider.GOOGLE)) {
            throw new AuthException(AuthErrorCode.SOCIAL_ACCOUNT_ALREADY_LINKED);
        }
        if (socialAccountRepository.findByProviderAndProviderUserId(MemberProvider.GOOGLE, userInfo.sub()).isPresent()) {
            throw new AuthException(AuthErrorCode.SOCIAL_ACCOUNT_LINKED_TO_ANOTHER_MEMBER);
        }

        try {
            socialAccountRepository.saveAndFlush(OAuthConverter.toGoogleSocialAccount(member, userInfo));
            return member;
        } catch (DataIntegrityViolationException exception) {
            // 서로 다른 회원이 같은 Google 계정을 동시에 연결한 경우 DB 유니크 제약으로 차단
            throw new AuthException(AuthErrorCode.SOCIAL_ACCOUNT_LINKED_TO_ANOTHER_MEMBER);
        }
    }

    private Member getActiveMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        validateActiveMember(member);
        return member;
    }

    private Member getActiveMemberForUpdate(Long memberId) {
        Member member = memberRepository.findByIdForUpdate(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        validateActiveMember(member);
        return member;
    }

    private void validateActiveMember(Member member) {
        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
        }
    }
}
