package com.omo.backend.domain.auth.converter;

import com.omo.backend.domain.auth.dto.AuthResponseDTO;

public class AuthConverter {

    // entity -> 이메일 인증번호 발송 DTO
    public static AuthResponseDTO.EmailSendResultDTO toEmailSendResultDTO(String email, Long expiresInSeconds) {
        return AuthResponseDTO.EmailSendResultDTO.builder()
                .email(email)
                .expiresInSeconds(expiresInSeconds)
                .build();
    }

    // entity -> 이메일 인증번호 검증 DTO
    public static AuthResponseDTO.EmailVerifyResultDTO toEmailVerifyResultDTO(String email) {
        return AuthResponseDTO.EmailVerifyResultDTO.builder()
                .email(email)
                .verified(true)
                .build();
    }

    // entity -> 일반 로그인 DTO
    public static AuthResponseDTO.LoginResultDTO toLoginResultDTO(Long memberId, String accessToken, String refreshToken) {
        return AuthResponseDTO.LoginResultDTO.builder()
                .memberId(memberId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
