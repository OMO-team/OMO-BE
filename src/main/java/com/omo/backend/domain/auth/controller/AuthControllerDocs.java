package com.omo.backend.domain.auth.controller;

import com.omo.backend.domain.auth.dto.AuthRequestDTO;
import com.omo.backend.domain.auth.dto.AuthResponseDTO;
import com.omo.backend.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "인증 API")
public interface AuthControllerDocs {

    @Operation(summary = "이메일 인증번호 발송", description = "입력한 이메일로 6자리 인증번호를 발송합니다.")
    ApiResponse<AuthResponseDTO.EmailSendResultDTO> sendEmailVerificationCode(
            @RequestBody AuthRequestDTO.EmailSendDTO request
    );

    @Operation(summary = "이메일 인증번호 검증", description = "이메일과 인증번호를 검증하고 인증 완료 상태를 Redis에 저장합니다.")
    ApiResponse<AuthResponseDTO.EmailVerifyResultDTO> verifyEmailVerificationCode(
            @RequestBody AuthRequestDTO.EmailVerifyDTO request
    );
}
