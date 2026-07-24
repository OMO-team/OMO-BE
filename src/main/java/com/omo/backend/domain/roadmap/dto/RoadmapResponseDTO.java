package com.omo.backend.domain.roadmap.dto;

import com.omo.backend.domain.task.enums.TaskCategory;
import com.omo.backend.domain.task.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

public class RoadmapResponseDTO {

    private RoadmapResponseDTO() {
    }

    @Builder
    public record CreateResultDTO(
            @Schema(description = "생성된 로드맵 ID", example = "1")
            Long roadmapId,

            @Schema(description = "로드맵 제목", example = "도쿄 워킹홀리데이 로드맵")
            String title,

            @Schema(description = "도시 ID", example = "5")
            Long cityId,

            @Schema(description = "목적 ID", example = "2")
            Long purposeId,

            @Schema(description = "출국일(최초 생성 시 미설정)", nullable = true)
            LocalDate departureDate,

            @Schema(description = "생성된 전체 태스크 수", example = "12")
            Integer taskCount
    ) {
    }

    @Builder
    public record ListItemDTO(
            Long roadmapId,
            String title,
            Long cityId,
            String cityName,
            Long purposeId,
            String purposeName,
            LocalDate departureDate,
            Integer stayMonths,
            Long departureDDay,
            Long completedTaskCount,
            Long totalTaskCount,
            Double progressRate,
            Long nextTaskId,
            String nextTaskName,
            LocalDate nextScheduleDate,
            Long nextScheduleDDay,
            Boolean isNextScheduleOverdue
    ) {
    }

    @Builder
    public record DetailResultDTO(
            Long roadmapId,
            String title,
            Long cityId,
            String cityName,
            Long purposeId,
            String purposeName,
            LocalDate departureDate,
            Integer stayMonths,
            Long departureDDay,
            Long completedTaskCount,
            Long totalTaskCount,
            Double progressRate,
            Long nextTaskId,
            String nextTaskName,
            LocalDate nextScheduleDate,
            Long nextScheduleDDay,
            Boolean isNextScheduleOverdue,
            BudgetDTO budget,
            List<TaskItemDTO> tasks
    ) {
    }

    @Builder
    public record BudgetDTO(
            Long initialSettlementCost,
            Long monthlyCost,
            Long totalCost
    ) {
    }

    @Builder
    public record TaskItemDTO(
            Long taskId,
            String name,
            TaskCategory category,
            LocalDate dueDate,
            Long scheduleDDay,
            Boolean isOverdue,
            TaskStatus status,
            Boolean isCompleted
    ) {
    }

    @Builder
    public record TaskScheduleDTO(
            Long taskId,
            LocalDate dueDate
    ) {
    }

    @Builder
    public record UpdateScheduleResultDTO(
            Long roadmapId,
            LocalDate departureDate,
            Long departureDDay,
            List<TaskScheduleDTO> taskSchedules
    ) {
    }
}
