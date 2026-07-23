package com.omo.backend.domain.roadmap.converter;

import com.omo.backend.domain.roadmap.dto.RoadmapResponseDTO;
import com.omo.backend.domain.roadmap.entity.Roadmap;
import com.omo.backend.domain.task.entity.Task;
import com.omo.backend.domain.task.enums.TaskStatus;
import java.time.LocalDate;
import java.util.List;

public final class RoadmapConverter {

    private RoadmapConverter() {
    }

    public static RoadmapResponseDTO.CreateResultDTO toCreateResultDTO(
            Roadmap roadmap,
            int taskCount
    ) {
        return RoadmapResponseDTO.CreateResultDTO.builder()
                .roadmapId(roadmap.getId())
                .title(roadmap.getTitle())
                .cityId(roadmap.getRoadmapTemplate().getCity().getCityId())
                .purposeId(roadmap.getRoadmapTemplate().getPurpose().getPurposeId())
                .departureDate(roadmap.getDepartureDate())
                .taskCount(taskCount)
                .build();
    }

    public static RoadmapResponseDTO.ListItemDTO toListItemDTO(
            Roadmap roadmap,
            long completedTaskCount,
            long totalTaskCount,
            double progressRate,
            Task nextTask,
            Task nextScheduleTask,
            Long departureDDay,
            Long nextScheduleDDay,
            Boolean isNextScheduleOverdue
    ) {
        return RoadmapResponseDTO.ListItemDTO.builder()
                .roadmapId(roadmap.getId())
                .title(roadmap.getTitle())
                .cityId(roadmap.getRoadmapTemplate().getCity().getCityId())
                .cityName(roadmap.getRoadmapTemplate().getCity().getName())
                .purposeId(roadmap.getRoadmapTemplate().getPurpose().getPurposeId())
                .purposeName(roadmap.getRoadmapTemplate().getPurpose().getName())
                .departureDate(roadmap.getDepartureDate())
                .stayMonths(roadmap.getStayMonths())
                .departureDDay(departureDDay)
                .completedTaskCount(completedTaskCount)
                .totalTaskCount(totalTaskCount)
                .progressRate(progressRate)
                .nextTaskId(nextTask == null ? null : nextTask.getId())
                .nextTaskName(nextTask == null ? null : nextTask.getName())
                .nextScheduleDate(nextScheduleTask == null ? null : nextScheduleTask.getDueDate())
                .nextScheduleDDay(nextScheduleDDay)
                .isNextScheduleOverdue(isNextScheduleOverdue)
                .build();
    }

    public static RoadmapResponseDTO.TaskItemDTO toTaskItemDTO(
            Task task,
            TaskStatus status,
            Long scheduleDDay,
            Boolean isOverdue
    ) {
        return RoadmapResponseDTO.TaskItemDTO.builder()
                .taskId(task.getId())
                .name(task.getName())
                .category(task.getCategory())
                .dueDate(task.getDueDate())
                .scheduleDDay(scheduleDDay)
                .isOverdue(isOverdue)
                .status(status)
                .isCompleted(task.isCompleted())
                .build();
    }

    public static RoadmapResponseDTO.DetailResultDTO toDetailResultDTO(
            Roadmap roadmap,
            long completedTaskCount,
            long totalTaskCount,
            double progressRate,
            Task nextTask,
            Task nextScheduleTask,
            Long departureDDay,
            Long nextScheduleDDay,
            Boolean isNextScheduleOverdue,
            List<RoadmapResponseDTO.TaskItemDTO> tasks
    ) {
        return RoadmapResponseDTO.DetailResultDTO.builder()
                .roadmapId(roadmap.getId())
                .title(roadmap.getTitle())
                .cityId(roadmap.getRoadmapTemplate().getCity().getCityId())
                .cityName(roadmap.getRoadmapTemplate().getCity().getName())
                .purposeId(roadmap.getRoadmapTemplate().getPurpose().getPurposeId())
                .purposeName(roadmap.getRoadmapTemplate().getPurpose().getName())
                .departureDate(roadmap.getDepartureDate())
                .stayMonths(roadmap.getStayMonths())
                .departureDDay(departureDDay)
                .completedTaskCount(completedTaskCount)
                .totalTaskCount(totalTaskCount)
                .progressRate(progressRate)
                .nextTaskId(nextTask == null ? null : nextTask.getId())
                .nextTaskName(nextTask == null ? null : nextTask.getName())
                .nextScheduleDate(nextScheduleTask == null ? null : nextScheduleTask.getDueDate())
                .nextScheduleDDay(nextScheduleDDay)
                .isNextScheduleOverdue(isNextScheduleOverdue)
                .tasks(tasks)
                .build();
    }

    public static RoadmapResponseDTO.UpdateScheduleResultDTO toUpdateScheduleResultDTO(
            Roadmap roadmap,
            Long departureDDay,
            List<Task> tasks
    ) {
        List<RoadmapResponseDTO.TaskScheduleDTO> taskSchedules = tasks.stream()
                .map(task -> RoadmapResponseDTO.TaskScheduleDTO.builder()
                        .taskId(task.getId())
                        .recommendedCompletionDate(task.getDueDate())
                        .build())
                .toList();

        return RoadmapResponseDTO.UpdateScheduleResultDTO.builder()
                .roadmapId(roadmap.getId())
                .departureDate(roadmap.getDepartureDate())
                .departureDDay(departureDDay)
                .taskSchedules(taskSchedules)
                .build();
    }
}
