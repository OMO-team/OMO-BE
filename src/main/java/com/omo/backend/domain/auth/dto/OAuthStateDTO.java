package com.omo.backend.domain.auth.dto;

import com.omo.backend.domain.auth.enums.OAuthPurpose;

import java.util.List;

// Google OAuth 요청의 목적과 회원가입 약관 또는 계정 연결 회원 정보를 Redis에 임시 저장
public class OAuthStateDTO {

    public record GoogleOAuthStateDTO(
            OAuthPurpose purpose,
            List<Long> agreedTermsIds,
            Long memberId
    ) {}
}
