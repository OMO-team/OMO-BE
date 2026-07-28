package com.omo.backend.domain.task.dto;

import com.omo.backend.domain.document.dto.TaskDocumentResponseDTO;
import com.omo.backend.domain.task.enums.TaskCategory;
import com.omo.backend.domain.task.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public class TaskResponseDTO {

    private TaskResponseDTO() {
    }

    @Builder
    public record CompleteResultDTO(
            @Schema(description = "완료된 태스크 ID", example = "10")
            Long taskId,

            @Schema(description = "실제 완료 여부", example = "true")
            Boolean isCompleted,

            @Schema(description = "완료 시각", example = "2026-07-21T15:30:00")
            LocalDateTime completedAt,

            @Schema(description = "완료 후 계산 상태", example = "COMPLETED")
            TaskStatus status
    ) {
    }

    @Builder
    public record DetailResultDTO(
            Long taskId,
            Long roadmapId,
            String name,
            String description,
            TaskCategory category,
            Integer displayOrder,
            TaskStatus status,
            Boolean isCompleted,
            LocalDate dueDate,
            Long scheduleDDay,
            Boolean isOverdue,
            LocalDateTime completedAt,
            Long completedDocumentCount,
            Long totalDocumentCount,
            List<TaskDocumentResponseDTO.DocumentItemDTO> documents
    ) {
    }

    @Builder
    public record UpdateScheduleResultDTO(
            @Schema(description = "태스크 ID", example = "10")
            Long taskId,

            @Schema(description = "변경된 권장 완료일", example = "2026-12-01")
            LocalDate dueDate,

            @Schema(description = "권장 완료일까지 남은 일수", example = "45")
            Long scheduleDDay,

            @Schema(description = "일정 초과 여부", example = "false")
            Boolean isOverdue
    ) {
    }
}
