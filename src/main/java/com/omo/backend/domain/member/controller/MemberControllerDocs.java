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

    @Operation(summary = "프로필 수정", description = "로그인한 회원의 이름을 수정합니다.")
    ApiResponse<MemberResponseDTO.UpdateProfileResultDTO> updateProfile(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MemberRequestDTO.UpdateProfileDTO request
    );

    @Operation(summary = "프로필 이미지 업로드 URL 발급", description = "프로필 이미지를 S3에 직접 업로드할 수 있는 임시 PUT URL을 발급합니다.")
    ApiResponse<MemberResponseDTO.ProfileImageUploadUrlResultDTO> createProfileImageUploadUrl(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MemberRequestDTO.ProfileImageUploadUrlDTO request
    );

    @Operation(
            summary = "프로필 이미지 등록 및 교체",
            description = """
                    프로필 이미지 업로드가 완료된 뒤, 업로드 URL 발급 API에서 받은 objectKey를 전달해 등록합니다.

                    프론트 연동 순서:
                    1. POST /api/v1/members/me/profile-image/upload-url을 호출합니다.
                    2. 응답의 uploadUrl로 이미지 파일을 PUT 업로드합니다.
                    3. PUT 요청의 Content-Type은 URL 발급 응답의 contentType과 동일해야 합니다.
                    4. 업로드 성공 후 응답의 objectKey를 이 API에 전달합니다.

                    백엔드는 objectKey가 로그인 회원의 경로인지, S3 객체가 실제로 존재하는지, 파일 크기와 MIME 타입이 허용 범위인지 확인한 뒤 프로필 이미지로 등록합니다.
                    기존 프로필 이미지가 있으면 새 이미지로 교체하고 기존 S3 객체를 삭제합니다.
                    """
    )
    ApiResponse<MemberResponseDTO.ProfileImageUpdateResultDTO> updateProfileImage(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MemberRequestDTO.ProfileImageUpdateDTO request
    );

    @Operation(
            summary = "프로필 이미지 삭제",
            description = "로그인한 회원의 프로필 이미지 연결을 제거하고 기존 S3 객체를 삭제합니다. 이미지가 없어도 성공 처리합니다."
    )
    ApiResponse<Void> deleteProfileImage(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
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
