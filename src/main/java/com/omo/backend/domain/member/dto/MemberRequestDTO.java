package com.omo.backend.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public class MemberRequestDTO {

    // 일반 회원가입
    public record JoinDTO(
            @Schema(description = "회원 이름", example = "홍길동")
            @NotBlank(message = "이름은 필수 입력값입니다.")
            @Size(max = 20, message = "이름은 20자 이하로 입력해 주세요.")
            String name,

            @Schema(description = "회원 이메일", example = "example@email.com")
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Email(message = "올바른 이메일 형식을 입력해 주세요.")
            @Size(max = 100, message = "이메일은 100자 이하로 입력해 주세요.")
            String email,

            @Schema(description = "비밀번호", example = "Password1234!")
            @NotBlank(message = "비밀번호는 필수 입력값입니다.")
            @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해 주세요.")
            @Pattern(
                    regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,20}$|^(?=.*[A-Za-z])(?=.*[!@#$%^&*()_+=\\-\\[\\]{};':\"\\\\|,.<>/?]).{8,20}$|^(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-\\[\\]{};':\"\\\\|,.<>/?]).{8,20}$",
                    message = "비밀번호는 영문, 숫자, 특수문자 중 2종류 이상을 조합해 주세요."
            )
            String password,

            @Schema(description = "비밀번호 확인", example = "Password1234!")
            @NotBlank(message = "비밀번호 확인은 필수 입력값입니다.")
            @Size(min = 8, max = 20, message = "비밀번호 확인은 8자 이상 20자 이하로 입력해 주세요.")
            String passwordConfirm,

            @Schema(description = "동의한 약관 ID 목록", example = "[1, 2]")
            @NotEmpty(message = "동의한 약관 목록은 필수 입력값입니다.")
            List<@NotNull(message = "약관 ID는 null일 수 없습니다.") Long> agreedTermsIds
    ) {}

    // 프로필 수정
    public record UpdateProfileDTO(
            @Schema(description = "회원 이름", example = "홍길동")
            @NotBlank(message = "이름은 필수 입력값입니다.")
            @Size(max = 20, message = "이름은 20자 이하로 입력해 주세요.")
            String name
    ) {}

    // 프로필 이미지 업로드 URL 발급
    public record ProfileImageUploadUrlDTO(
            @Schema(description = "업로드할 파일명", example = "profile.jpg")
            @NotBlank(message = "파일명은 필수 입력값입니다.")
            String fileName,

            @Schema(description = "파일 MIME 타입", example = "image/jpeg")
            @NotBlank(message = "파일 MIME 타입은 필수 입력값입니다.")
            String contentType,

            @Schema(description = "파일 크기(byte)", example = "1048576")
            @NotNull(message = "파일 크기는 필수 입력값입니다.")
            @Positive(message = "파일 크기는 0보다 커야 합니다.")
            @Max(value = 5L * 1024 * 1024, message = "파일 크기는 5MB 이하여야 합니다.")
            Long fileSize
    ) {}

    // 프로필 이미지 등록
    public record ProfileImageUpdateDTO(
            @Schema(description = "업로드 URL 발급 API에서 반환된 S3 object key", example = "profiles/1/550e8400-e29b-41d4-a716-446655440000.jpg")
            @NotBlank(message = "프로필 이미지 object key는 필수 입력값입니다.")
            @Size(max = 500, message = "프로필 이미지 object key는 500자 이하여야 합니다.")
            String objectKey
    ) {}

    // 내 설정 수정
    public record UpdateSettingsDTO(
            @Schema(description = "푸쉬 알림 여부", example = "true")
            Boolean pushNotification,

            @Schema(description = "이메일 알림 여부", example = "true")
            Boolean emailNotification,

            @Schema(description = "자동 저장 여부", example = "true")
            Boolean autoSave,

            @Schema(description = "2단계 인증 활성화 여부", example = "false")
            Boolean twoFactorEnabled
    ) {}

    // 비밀번호 변경
    public record ChangePasswordDTO(
            @Schema(description = "현재 비밀번호", example = "Password1234!")
            @NotBlank(message = "현재 비밀번호는 필수 입력값입니다.")
            @Size(min = 8, max = 20, message = "현재 비밀번호는 8자 이상 20자 이하로 입력해 주세요.")
            String currentPassword,

            @Schema(description = "새 비밀번호", example = "NewPassword1234!")
            @NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
            @Size(min = 8, max = 20, message = "새 비밀번호는 8자 이상 20자 이하로 입력해 주세요.")
            @Pattern(
                    regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,20}$|^(?=.*[A-Za-z])(?=.*[!@#$%^&*()_+=\\-\\[\\]{};':\"\\\\|,.<>/?]).{8,20}$|^(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-\\[\\]{};':\"\\\\|,.<>/?]).{8,20}$",
                    message = "새 비밀번호는 영문, 숫자, 특수문자 중 2종류 이상을 조합해 주세요."
            )
            String newPassword,

            @Schema(description = "새 비밀번호 확인", example = "NewPassword1234!")
            @NotBlank(message = "새 비밀번호 확인은 필수 입력값입니다.")
            @Size(min = 8, max = 20, message = "새 비밀번호 확인은 8자 이상 20자 이하로 입력해 주세요.")
            String newPasswordConfirm
    ) {}
}
