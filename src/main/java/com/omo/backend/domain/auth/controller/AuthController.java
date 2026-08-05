package com.omo.backend.domain.auth.controller;

import com.omo.backend.domain.auth.dto.AuthRequestDTO;
import com.omo.backend.domain.auth.dto.AuthResponseDTO;
import com.omo.backend.domain.auth.dto.OAuthRequestDTO;
import com.omo.backend.domain.auth.dto.OAuthResponseDTO;
import com.omo.backend.domain.auth.exception.AuthErrorCode;
import com.omo.backend.domain.auth.exception.AuthException;
import com.omo.backend.domain.auth.service.AuthCommandService;
import com.omo.backend.domain.auth.service.EmailVerificationService;
import com.omo.backend.domain.auth.service.GoogleOAuthService;
import com.omo.backend.global.apiPayload.ApiResponse;
import com.omo.backend.global.apiPayload.code.GeneralErrorCode;
import com.omo.backend.global.apiPayload.exception.GeneralException;
import com.omo.backend.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth/v1")
public class AuthController implements AuthControllerDocs {

    private static final String BEARER_PREFIX = "Bearer ";

    private final EmailVerificationService emailVerificationService;
    private final AuthCommandService authCommandService;
    private final GoogleOAuthService googleOAuthService;

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

    @PatchMapping("/password/reset")
    public ApiResponse<Void> resetPassword(
            @Valid @RequestBody AuthRequestDTO.PasswordResetDTO request
    ) {
        authCommandService.resetPassword(request);
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/login/local")
    public ApiResponse<AuthResponseDTO.LoginResultDTO> doLogin(
            @RequestBody @Valid AuthRequestDTO.LoginDTO request
    ) {
        AuthResponseDTO.LoginResultDTO result = authCommandService.login(request);
        return ApiResponse.onSuccess(result);
    }

    @PostMapping("/oauth/google/signup")
    public ApiResponse<OAuthResponseDTO.GoogleAuthorizationUrlDTO> doGoogleSignup(
            @Valid @RequestBody OAuthRequestDTO.GoogleSignupStartDTO request
    ) {
        OAuthResponseDTO.GoogleAuthorizationUrlDTO result = googleOAuthService.createSignupAuthorizationUrl(request);
        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/oauth/google/login")
    public ApiResponse<OAuthResponseDTO.GoogleAuthorizationUrlDTO> doGoogleLogin() {
        OAuthResponseDTO.GoogleAuthorizationUrlDTO result = googleOAuthService.createLoginAuthorizationUrl();
        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/oauth/google/callback")
    public ResponseEntity<Void> doGoogleCallback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error
    ) {
        try {
            return redirect(googleOAuthService.handleCallback(code, state, error));
        } catch (Exception exception) {
            String errorCode = resolveOAuthErrorCode(exception);
            return redirect(googleOAuthService.createCallbackErrorRedirectUrl(errorCode));
        }
    }

    @GetMapping("/oauth/google/link/callback")
    public ResponseEntity<Void> doGoogleLinkCallback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error
    ) {
        try {
            return redirect(googleOAuthService.handleLinkCallback(code, state, error));
        } catch (Exception exception) {
            String errorCode = resolveOAuthErrorCode(exception);
            return redirect(googleOAuthService.createLinkCallbackErrorRedirectUrl(errorCode));
        }
    }

    @PostMapping("/oauth/google/exchange")
    public ApiResponse<AuthResponseDTO.LoginResultDTO> exchangeGoogleLoginTicket(
            @Valid @RequestBody OAuthRequestDTO.GoogleLoginExchangeDTO request
    ) {
        AuthResponseDTO.LoginResultDTO result = googleOAuthService.exchangeLoginTicket(request);
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

    private ResponseEntity<Void> redirect(String redirectUrl) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(redirectUrl))
                .build();
    }

    private String resolveOAuthErrorCode(Exception exception) {
        if (exception instanceof GeneralException generalException) {
            return generalException.getBaseErrorCode().getCode();
        }

        log.error("Google OAuth 콜백 처리 중 예상하지 못한 오류 발생", exception);
        return GeneralErrorCode.INTERNAL_SERVER_ERROR.getCode();
    }
}
