package com.omo.backend.domain.inquiry.dto;

import com.omo.backend.domain.inquiry.enums.InquiryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class InquiryRequestDTO {

    // 1:1 문의
    public record InquiryDTO(
            @Schema(description = "문의 유형", example = "BUG_REPORT")
            @NotNull(message = "문의 유형은 필수 입력값입니다.")
            InquiryType type,

            @Schema(description = "문의자 이름", example = "홍길동")
            @NotBlank(message = "이름은 필수 입력값입니다.")
            @Size(max = 20, message = "이름은 20자 이하로 입력해 주세요.")
            String name,

            @Schema(description = "답변을 받을 이메일", example = "example@email.com")
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Email(message = "올바른 이메일 형식을 입력해 주세요.")
            @Size(max = 100, message = "이메일은 100자 이하로 입력해 주세요.")
            String email,

            @Schema(description = "문의 내용", example = "로그인 중 오류가 발생합니다.")
            @NotBlank(message = "문의 내용은 필수 입력값입니다.")
            @Size(min = 10, max = 1000, message = "문의 내용은 10자 이상 1,000자 이하로 입력해 주세요.")
            String content
    ) {}
}
