package com.omo.backend.domain.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReportRequestDTO {

    public record AiReportRequestDTO(
            @Schema(description = "질문 내용", example = "시드니 물가는 어때?")
            @NotBlank(message = "질문을 입력해주세요.")
            String question
    ) {}

    public record AddCompareItemDTO(
            @Schema(description = "담을 도시 ID", example = "1")
            @NotNull(message = "도시 ID는 필수입니다.")
            Long cityId
    ) {}
}
