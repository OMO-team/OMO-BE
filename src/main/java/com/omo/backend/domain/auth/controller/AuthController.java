package com.omo.backend.domain.auth.controller;

import com.omo.backend.domain.auth.dto.AuthRequestDTO;
import com.omo.backend.domain.auth.dto.AuthResponseDTO;
import com.omo.backend.domain.auth.service.EmailVerificationService;
import com.omo.backend.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/v1/email")
public class AuthController implements AuthControllerDocs {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send")
    public ApiResponse<AuthResponseDTO.EmailSendResultDTO> sendEmailVerificationCode(
            @Valid @RequestBody AuthRequestDTO.EmailSendDTO request
    ) {
        AuthResponseDTO.EmailSendResultDTO result = emailVerificationService.send(request);
        return ApiResponse.onSuccess(result);
    }

    @PostMapping("/verify")
    public ApiResponse<AuthResponseDTO.EmailVerifyResultDTO> verifyEmailVerificationCode(
            @Valid @RequestBody AuthRequestDTO.EmailVerifyDTO request
    ) {
        AuthResponseDTO.EmailVerifyResultDTO result = emailVerificationService.verify(request);
        return ApiResponse.onSuccess(result);
    }
}
