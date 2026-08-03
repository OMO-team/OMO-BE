package com.omo.backend.domain.auth.service;

import com.omo.backend.domain.auth.converter.OAuthConverter;
import com.omo.backend.domain.auth.dto.OAuthResponseDTO;
import com.omo.backend.domain.auth.exception.AuthErrorCode;
import com.omo.backend.domain.auth.exception.AuthException;
import com.omo.backend.domain.member.entity.Member;
import com.omo.backend.domain.member.enums.MemberProvider;
import com.omo.backend.domain.member.enums.MemberStatus;
import com.omo.backend.domain.member.exception.MemberErrorCode;
import com.omo.backend.domain.member.exception.MemberException;
import com.omo.backend.domain.member.repository.MemberRepository;
import com.omo.backend.domain.member.repository.SocialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GoogleOAuthPersistenceService {

    private final MemberRepository memberRepository;
    private final SocialAccountRepository socialAccountRepository;

    // 로그인 회원과 Google 계정의 중복 연결을 검증하고 소셜 계정 저장
    @Transactional
    public Member linkGoogleAccount(Long memberId, OAuthResponseDTO.GoogleUserInfoDTO userInfo) {
        Member member = getActiveMember(memberId);

        if (socialAccountRepository.existsByMemberIdAndProvider(member.getId(), MemberProvider.GOOGLE)) {
            throw new AuthException(AuthErrorCode.SOCIAL_ACCOUNT_ALREADY_LINKED);
        }
        if (socialAccountRepository.findByProviderAndProviderUserId(MemberProvider.GOOGLE, userInfo.sub()).isPresent()) {
            throw new AuthException(AuthErrorCode.SOCIAL_ACCOUNT_LINKED_TO_ANOTHER_MEMBER);
        }

        socialAccountRepository.save(OAuthConverter.toGoogleSocialAccount(member, userInfo));
        return member;
    }

    private Member getActiveMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
        }
        return member;
    }
}
