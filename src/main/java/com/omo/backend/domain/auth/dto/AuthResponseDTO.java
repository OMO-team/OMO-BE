package com.omo.backend.domain.auth.dto;

import lombok.Builder;

public class AuthResponseDTO {

    // 이메일 인증번호 발송 결과
    @Builder
    public record EmailSendResultDTO(
            String email,
            Long expiresInSeconds
    ) {}

    // 이메일 인증번호 검증 결과
    @Builder
    public record EmailVerifyResultDTO(
            String email,
            Boolean verified
    ) {}

    // 일반 로그인 결과
    @Builder
    public record LoginResultDTO(
            Long memberId,
            String accessToken,
            String refreshToken
    ) {}
}
