package com.omo.backend.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
            String password,

            @Schema(description = "동의한 약관 ID 목록", example = "[1, 2]")
            @NotEmpty(message = "동의한 약관 목록은 필수 입력값입니다.")
            List<@NotNull(message = "약관 ID는 null일 수 없습니다.") Long> agreedTermsIds
    ) {}
}
