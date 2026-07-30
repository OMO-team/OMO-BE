package com.omo.backend.domain.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class TaskRequestDTO {

    private TaskRequestDTO() {
    }

    @Schema(name = "TaskUpdateScheduleRequest")
    public record UpdateScheduleDTO(
            @Schema(description = "태스크 권장 완료일", example = "2026-12-01")
            @NotNull(message = "태스크 권장 완료일은 필수입니다.")
            LocalDate dueDate
    ) {
    }
}
