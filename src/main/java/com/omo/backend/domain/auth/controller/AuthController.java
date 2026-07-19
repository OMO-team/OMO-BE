package com.omo.backend.domain.auth.controller;

import com.omo.backend.domain.auth.dto.AuthRequestDTO;
import com.omo.backend.domain.auth.dto.AuthResponseDTO;
import com.omo.backend.domain.auth.exception.AuthErrorCode;
import com.omo.backend.domain.auth.exception.AuthException;
import com.omo.backend.domain.auth.service.AuthCommandService;
import com.omo.backend.domain.auth.service.EmailVerificationService;
import com.omo.backend.global.apiPayload.ApiResponse;
import com.omo.backend.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/v1")
public class AuthController implements AuthControllerDocs {

    private static final String BEARER_PREFIX = "Bearer ";

    private final EmailVerificationService emailVerificationService;
    private final AuthCommandService authCommandService;

    @PostMapping("/email/send")
    public ApiResponse<AuthResponseDTO.EmailSendResultDTO> sendEmailVerificationCode(
            @Valid @RequestBody AuthRequestDTO.EmailSendDTO request
    ) {
        AuthResponseDTO.EmailSendResultDTO result = emailVerificationService.send(request);
        return ApiResponse.onSuccess(result);
    }

    @PostMapping("/email/verify")
    public ApiResponse<AuthResponseDTO.EmailVerifyResultDTO> verifyEmailVerificationCode(
            @Valid @RequestBody AuthRequestDTO.EmailVerifyDTO request
    ) {
        AuthResponseDTO.EmailVerifyResultDTO result = emailVerificationService.verify(request);
        return ApiResponse.onSuccess(result);
    }

    @PostMapping("/password/reset/email")
    public ApiResponse<AuthResponseDTO.EmailSendResultDTO> sendPasswordResetVerificationCode(
            @Valid @RequestBody AuthRequestDTO.PasswordResetEmailSendDTO request
    ) {
        AuthResponseDTO.EmailSendResultDTO result = emailVerificationService.sendPasswordReset(request);
        return ApiResponse.onSuccess(result);
    }

    @PostMapping("/password/reset/verify")
    public ApiResponse<AuthResponseDTO.EmailVerifyResultDTO> verifyPasswordResetVerificationCode(
            @Valid @RequestBody AuthRequestDTO.PasswordResetEmailVerifyDTO request
    ) {
        AuthResponseDTO.EmailVerifyResultDTO result = emailVerificationService.verifyPasswordReset(request);
        return ApiResponse.onSuccess(result);
    }

    @PostMapping("/login/local")
    public ApiResponse<AuthResponseDTO.LoginResultDTO> doLogin(
            @RequestBody @Valid AuthRequestDTO.LoginDTO request
    ) {
        AuthResponseDTO.LoginResultDTO result = authCommandService.login(request);
        return ApiResponse.onSuccess(result);
    }

    @PostMapping("/reissue")
    public ApiResponse<AuthResponseDTO.ReissueResultDTO> doReissue(
            @RequestBody @Valid AuthRequestDTO.ReissueDTO request
    ) {
        AuthResponseDTO.ReissueResultDTO result = authCommandService.reissue(request);
        return ApiResponse.onSuccess(result);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> doLogout(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        authCommandService.logout(userDetails.getMemberId(), extractToken(authorizationHeader));
        return ApiResponse.onSuccess(null);
    }

    // Authorization 헤더에서 토큰 추출
    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN_FORMAT);
        }
        return authorizationHeader.substring(BEARER_PREFIX.length());
    }
}
