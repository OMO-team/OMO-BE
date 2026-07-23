package com.omo.backend.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AuthRequestDTO {

    // 이메일 인증번호 발송
    public record EmailSendDTO(
            @Schema(description = "인증번호를 받을 이메일", example = "example@email.com")
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Email(message = "올바른 이메일 형식을 입력해 주세요.")
            @Size(max = 100, message = "이메일은 100자 이하로 입력해 주세요.")
            String email
    ) {}

    // 이메일 인증번호 검증
    public record EmailVerifyDTO(
            @Schema(description = "인증할 이메일", example = "example@email.com")
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Email(message = "올바른 이메일 형식을 입력해 주세요.")
            @Size(max = 100, message = "이메일은 100자 이하로 입력해 주세요.")
            String email,

            @Schema(description = "이메일 인증번호", example = "123456")
            @NotBlank(message = "인증번호는 필수 입력값입니다.")
            @Size(min = 6, max = 6, message = "인증번호는 6자리로 입력해 주세요.")
            String code
    ) {}

    // 일반 로그인
    public record LoginDTO(
            @Schema(description = "회원 이메일", example = "example@email.com")
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Email(message = "올바른 이메일 형식을 입력해 주세요.")
            @Size(max = 100, message = "이메일은 100자 이하로 입력해 주세요.")
            String email,

            @Schema(description = "비밀번호", example = "Password1234!")
            @NotBlank(message = "비밀번호는 필수 입력값입니다.")
            @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해 주세요.")
            String password
    ) {}

    // 토큰 재발급
    public record ReissueDTO(
            @Schema(description = "재발급에 사용할 리프레시 토큰")
            @NotBlank(message = "리프레시 토큰은 필수 입력값입니다.")
            String refreshToken
    ) {}

    // 비밀번호 찾기 인증번호 발송
    public record PasswordResetEmailSendDTO(
            @Schema(description = "가입한 회원 이메일", example = "example@email.com")
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Email(message = "올바른 이메일 형식을 입력해 주세요.")
            @Size(max = 100, message = "이메일은 100자 이하로 입력해 주세요.")
            String email
    ) {}

    // 비밀번호 찾기 인증번호 검증
    public record PasswordResetEmailVerifyDTO(
            @Schema(description = "가입한 회원 이메일", example = "example@email.com")
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Email(message = "올바른 이메일 형식을 입력해 주세요.")
            @Size(max = 100, message = "이메일은 100자 이하로 입력해 주세요.")
            String email,

            @Schema(description = "이메일 인증번호", example = "123456")
            @NotBlank(message = "인증번호는 필수 입력값입니다.")
            @Size(min = 6, max = 6, message = "인증번호는 6자리로 입력해 주세요.")
            String code
    ) {}

    // 새 비밀번호 설정
    public record PasswordResetDTO(
            @Schema(description = "가입한 회원 이메일", example = "example@email.com")
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Email(message = "올바른 이메일 형식을 입력해 주세요.")
            @Size(max = 100, message = "이메일은 100자 이하로 입력해 주세요.")
            String email,

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
