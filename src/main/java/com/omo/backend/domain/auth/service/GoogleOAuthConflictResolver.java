package com.omo.backend.domain.auth.service;

import com.omo.backend.domain.auth.dto.OAuthResponseDTO;
import com.omo.backend.domain.auth.exception.AuthErrorCode;
import com.omo.backend.domain.member.enums.MemberProvider;
import com.omo.backend.domain.member.repository.MemberRepository;
import com.omo.backend.domain.member.repository.SocialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleOAuthConflictResolver {

    private final MemberRepository memberRepository;
    private final SocialAccountRepository socialAccountRepository;

    // 실패한 저장 트랜잭션과 분리된 새 트랜잭션에서 동시 회원가입 충돌 원인 확인
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public Optional<AuthErrorCode> resolveSignupConflict(OAuthResponseDTO.GoogleUserInfoDTO userInfo) {
        if (socialAccountRepository.findByProviderAndProviderUserId(MemberProvider.GOOGLE, userInfo.sub()).isPresent()) {
            return Optional.of(AuthErrorCode.OAUTH_ACCOUNT_ALREADY_EXISTS);
        }

        if (memberRepository.existsByEmail(userInfo.email())) {
            return Optional.of(AuthErrorCode.OAUTH_ACCOUNT_LINK_REQUIRED);
        }

        return Optional.empty();
    }
}
