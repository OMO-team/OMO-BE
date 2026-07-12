package com.omo.backend.domain.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class ReportRequestDTO {

    public record AiReportRequestDTO(
            @Schema(description = "질문 내용", example = "시드니 물가는 어때?")
            @NotBlank(message = "질문을 입력해주세요.")
            String question
    ){}
}
