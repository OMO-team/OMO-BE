package com.omo.backend.domain.auth.controller;

import com.omo.backend.domain.auth.dto.AuthRequestDTO;
import com.omo.backend.domain.auth.dto.AuthResponseDTO;
import com.omo.backend.global.apiPayload.ApiResponse;
import com.omo.backend.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Auth", description = "인증 API")
public interface AuthControllerDocs {

    @Operation(summary = "이메일 인증번호 발송", description = "입력한 이메일로 6자리 인증번호를 발송합니다.")
    ApiResponse<AuthResponseDTO.EmailSendResultDTO> sendEmailVerificationCode(
            @Valid @RequestBody AuthRequestDTO.EmailSendDTO request
    );

    @Operation(summary = "이메일 인증번호 검증", description = "이메일과 인증번호를 검증하고 인증 완료 상태를 Redis에 저장합니다.")
    ApiResponse<AuthResponseDTO.EmailVerifyResultDTO> verifyEmailVerificationCode(
            @Valid @RequestBody AuthRequestDTO.EmailVerifyDTO request
    );

    @Operation(summary = "비밀번호 찾기 인증번호 발송", description = "가입된 회원 이메일로 비밀번호 찾기용 6자리 인증번호를 발송합니다.")
    ApiResponse<AuthResponseDTO.EmailSendResultDTO> sendPasswordResetVerificationCode(
            @Valid @RequestBody AuthRequestDTO.PasswordResetEmailSendDTO request
    );

    @Operation(summary = "비밀번호 찾기 인증번호 검증", description = "비밀번호 찾기용 인증번호를 검증하고 인증 완료 상태를 Redis에 저장합니다.")
    ApiResponse<AuthResponseDTO.EmailVerifyResultDTO> verifyPasswordResetVerificationCode(
            @Valid @RequestBody AuthRequestDTO.PasswordResetEmailVerifyDTO request
    );

    @Operation(summary = "일반 로그인", description = "이메일과 비밀번호로 로그인합니다.")
    ApiResponse<AuthResponseDTO.LoginResultDTO> doLogin(
            @Valid @RequestBody AuthRequestDTO.LoginDTO request
    );

    @Operation(summary = "토큰 재발급", description = "리프레시 토큰을 검증하고 새로운 액세스 토큰과 리프레시 토큰을 발급합니다.")
    ApiResponse<AuthResponseDTO.ReissueResultDTO> doReissue(
            @Valid @RequestBody AuthRequestDTO.ReissueDTO request
    );

    @Operation(summary = "로그아웃", description = "리프레시 토큰을 삭제하고 현재 액세스 토큰을 블랙리스트에 등록합니다.")
    ApiResponse<Void> doLogout(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(hidden = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    );
}
