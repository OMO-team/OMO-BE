package com.omo.backend.domain.task.converter;

import com.omo.backend.domain.task.dto.TaskResponseDTO;
import com.omo.backend.domain.task.entity.Task;
import com.omo.backend.domain.task.enums.TaskStatus;
import java.util.List;

public final class TaskConverter {

    private TaskConverter() {
    }

    public static TaskResponseDTO.TaskInfoDTO toTaskInfoDTO(
            Task task,
            TaskStatus status
    ) {
        return TaskResponseDTO.TaskInfoDTO.builder()
                .taskId(task.getId())
                .name(task.getName())
                .status(status)
                .isCompleted(task.isCompleted())
                .build();
    }

    public static TaskResponseDTO.TaskListResultDTO toTaskListResultDTO(
            Long roadmapId,
            List<TaskResponseDTO.TaskInfoDTO> tasks,
            long completedCount,
            double progressRate
    ) {
        return TaskResponseDTO.TaskListResultDTO.builder()
                .roadmapId(roadmapId)
                .completedCount(completedCount)
                .totalCount((long) tasks.size())
                .progressRate(progressRate)
                .tasks(tasks)
                .build();
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
}
