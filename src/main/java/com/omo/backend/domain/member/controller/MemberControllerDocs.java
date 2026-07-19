package com.omo.backend.domain.member.controller;

import com.omo.backend.domain.member.dto.MemberRequestDTO;
import com.omo.backend.domain.member.dto.MemberResponseDTO;
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

@Tag(name = "Member", description = "회원 API")
public interface MemberControllerDocs {

    @Operation(summary = "일반 회원가입", description = "이메일, 비밀번호, 이름, 약관 동의 목록으로 일반 회원가입을 진행합니다.")
    ApiResponse<MemberResponseDTO.JoinResultDTO> signup(
            @Valid @RequestBody MemberRequestDTO.JoinDTO request
    );

    @Operation(summary = "내 정보 조회", description = "로그인한 회원의 기본 정보를 조회합니다.")
    ApiResponse<MemberResponseDTO.MyInfoResultDTO> getMyInfo(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(summary = "프로필 수정", description = "로그인한 회원의 이름과 프로필 이미지를 수정합니다.")
    ApiResponse<MemberResponseDTO.UpdateProfileResultDTO> updateProfile(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MemberRequestDTO.UpdateProfileDTO request
    );

    @Operation(summary = "회원탈퇴", description = "로그인한 회원을 탈퇴 처리합니다.")
    ApiResponse<Void> withdraw(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(hidden = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    );

    @Operation(summary = "내 설정 조회", description = "로그인한 회원의 웹앱 설정을 조회합니다.")
    ApiResponse<MemberResponseDTO.SettingsResultDTO> getMySettings(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(summary = "내 설정 수정", description = "로그인한 회원의 웹앱 설정을 수정합니다.")
    ApiResponse<MemberResponseDTO.SettingsResultDTO> updateSettings(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MemberRequestDTO.UpdateSettingsDTO request
    );

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호를 확인한 뒤 새 비밀번호로 변경합니다.")
    ApiResponse<Void> changePassword(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MemberRequestDTO.ChangePasswordDTO request
    );
}
