package com.omo.backend.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OAuthRequestDTO {

    // Google 회원가입 시작
    public record GoogleSignupStartDTO(
            @Schema(description = "동의한 약관 ID 목록", example = "[1, 2]")
            @NotEmpty(message = "동의한 약관 목록은 필수 입력값입니다.")
            List<@NotNull(message = "약관 ID는 null일 수 없습니다.") Long> agreedTermsIds
    ) {}
}
