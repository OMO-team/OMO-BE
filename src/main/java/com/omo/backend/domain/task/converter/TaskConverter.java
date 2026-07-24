package com.omo.backend.domain.task.converter;

import com.omo.backend.domain.document.converter.TaskDocumentConverter;
import com.omo.backend.domain.document.entity.TaskDocument;
import com.omo.backend.domain.task.dto.TaskResponseDTO;
import com.omo.backend.domain.task.entity.Task;
import com.omo.backend.domain.task.enums.TaskStatus;
import java.util.List;

public final class TaskConverter {

    private TaskConverter() {
    }

    public static TaskResponseDTO.CompleteResultDTO toCompleteResultDTO(
            Task task,
            TaskStatus status
    ) {
        return TaskResponseDTO.CompleteResultDTO.builder()
                .taskId(task.getId())
                .isCompleted(task.isCompleted())
                .completedAt(task.getCompletedAt())
                .status(status)
                .build();
    }

    public static TaskResponseDTO.DetailResultDTO toDetailResultDTO(
            Task task,
            TaskStatus status,
            Long scheduleDDay,
            Boolean isOverdue,
            List<TaskDocument> documents
    ) {
        return TaskResponseDTO.DetailResultDTO.builder()
                .taskId(task.getId())
                .roadmapId(task.getRoadmap().getId())
                .name(task.getName())
                .description(task.getDescription())
                .category(task.getCategory())
                .displayOrder(task.getDisplayOrder())
                .status(status)
                .isCompleted(task.isCompleted())
                .dueDate(task.getDueDate())
                .scheduleDDay(scheduleDDay)
                .isOverdue(isOverdue)
                .completedAt(task.getCompletedAt())
                .completedDocumentCount(TaskDocumentConverter.countCompleted(documents))
                .totalDocumentCount(TaskDocumentConverter.countTotal(documents))
                .documents(documents.stream()
                        .map(TaskDocumentConverter::toDocumentItemDTO)
                        .toList())
                .build();
    }

    public static TaskResponseDTO.UpdateScheduleResultDTO toUpdateScheduleResultDTO(
            Task task,
            Long scheduleDDay,
            Boolean isOverdue
    ) {
        return TaskResponseDTO.UpdateScheduleResultDTO.builder()
                .taskId(task.getId())
                .dueDate(task.getDueDate())
                .scheduleDDay(scheduleDDay)
                .isOverdue(isOverdue)
                .build();
    }
}
