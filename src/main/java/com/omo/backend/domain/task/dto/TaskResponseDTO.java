package com.omo.backend.domain.task.dto;

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
    public record TaskInfoDTO(
            @Schema(description = "태스크 ID", example = "10")
            Long taskId,

            @Schema(description = "태스크 이름", example = "비자 신청")
            String name,

            @Schema(description = "현재 DB 사실로 계산한 상태", example = "IN_PROGRESS")
            TaskStatus status,

            @Schema(description = "실제 완료 여부", example = "false")
            Boolean isCompleted
    ) {
    }

    @Builder
    public record TaskListResultDTO(
            @Schema(description = "로드맵 ID", example = "1")
            Long roadmapId,

            @Schema(description = "완료된 태스크 수", example = "4")
            Long completedCount,

            @Schema(description = "전체 태스크 수", example = "10")
            Long totalCount,

            @Schema(description = "로드맵 진행률(%)", example = "40.0")
            Double progressRate,

            @Schema(description = "계산된 상태를 포함한 태스크 목록")
            List<TaskInfoDTO> tasks
    ) {
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
            Integer displayOrder,
            TaskStatus status,
            Boolean isCompleted,
            LocalDate recommendedCompletionDate,
            Long scheduleDDay,
            Boolean isOverdue,
            LocalDateTime completedAt
    ) {
    }
}
