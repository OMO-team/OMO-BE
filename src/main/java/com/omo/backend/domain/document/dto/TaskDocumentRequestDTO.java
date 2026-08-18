package com.omo.backend.domain.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class TaskDocumentRequestDTO {

    private TaskDocumentRequestDTO() {
    }

    public record UpdateCheckDTO(
            @Schema(description = "서류 완료 체크 여부", example = "true")
            @NotNull(message = "완료 여부는 필수 입력값입니다.")
            Boolean checked
    ) {
    }
}
