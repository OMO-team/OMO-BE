package com.omo.backend.domain.member.controller;

import com.omo.backend.domain.member.dto.MemberRequestDTO;
import com.omo.backend.domain.member.dto.MemberResponseDTO;
import com.omo.backend.domain.member.service.MemberCommandService;
import com.omo.backend.domain.member.service.MemberQueryService;
import com.omo.backend.domain.auth.exception.AuthErrorCode;
import com.omo.backend.domain.auth.exception.AuthException;
import com.omo.backend.domain.auth.service.AuthCommandService;
import com.omo.backend.global.apiPayload.ApiResponse;
import com.omo.backend.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController implements MemberControllerDocs {

    private static final String BEARER_PREFIX = "Bearer ";

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;
    private final AuthCommandService authCommandService;

    @PostMapping("/signup")
    public ApiResponse<MemberResponseDTO.JoinResultDTO> signup(
            @Valid @RequestBody MemberRequestDTO.JoinDTO request
    ) {
        MemberResponseDTO.JoinResultDTO result = memberCommandService.join(request);
        return ApiResponse.created(result);
    }

    @GetMapping("/me")
    public ApiResponse<MemberResponseDTO.MyInfoResultDTO> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        MemberResponseDTO.MyInfoResultDTO result = memberQueryService.getMyInfo(userDetails.getMemberId());
        return ApiResponse.onSuccess(result);
    }

    @PatchMapping("/me/profile")
    public ApiResponse<MemberResponseDTO.UpdateProfileResultDTO> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MemberRequestDTO.UpdateProfileDTO request
    ) {
        MemberResponseDTO.UpdateProfileResultDTO result = memberCommandService.updateProfile(userDetails.getMemberId(), request);
        return ApiResponse.onSuccess(result);
    }

    @DeleteMapping("/me")
    public ApiResponse<Void> withdraw(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        authCommandService.logout(userDetails.getMemberId(), extractToken(authorizationHeader));
        memberCommandService.withdrawMember(userDetails.getMemberId());
        return ApiResponse.onSuccess(null);
    }

    @GetMapping("/me/settings")
    public ApiResponse<MemberResponseDTO.SettingsResultDTO> getMySettings(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        MemberResponseDTO.SettingsResultDTO result = memberQueryService.getMySettings(userDetails.getMemberId());
        return ApiResponse.onSuccess(result);
    }

    @PatchMapping("/me/settings")
    public ApiResponse<MemberResponseDTO.SettingsResultDTO> updateSettings(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MemberRequestDTO.UpdateSettingsDTO request
    ) {
        MemberResponseDTO.SettingsResultDTO result = memberCommandService.updateSettings(userDetails.getMemberId(), request);
        return ApiResponse.onSuccess(result);
    }

    @PatchMapping("/me/password")
    public ApiResponse<Void> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MemberRequestDTO.ChangePasswordDTO request
    ) {
        memberCommandService.changePassword(userDetails.getMemberId(), request);
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
