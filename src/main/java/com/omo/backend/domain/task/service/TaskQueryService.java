package com.omo.backend.domain.task.service;

import com.omo.backend.domain.document.entity.TaskDocument;
import com.omo.backend.domain.document.repository.TaskDocumentRepository;
import com.omo.backend.domain.roadmap.exception.RoadmapErrorCode;
import com.omo.backend.domain.roadmap.exception.RoadmapException;
import com.omo.backend.domain.roadmap.repository.RoadmapRepository;
import com.omo.backend.domain.roadmap.service.RoadmapProgressCalculator;
import com.omo.backend.domain.roadmap.service.RoadmapScheduleCalculator;
import com.omo.backend.domain.task.converter.TaskConverter;
import com.omo.backend.domain.task.dto.TaskResponseDTO;
import com.omo.backend.domain.task.entity.Task;
import com.omo.backend.domain.task.entity.TaskDependency;
import com.omo.backend.domain.task.exception.TaskErrorCode;
import com.omo.backend.domain.task.exception.TaskException;
import com.omo.backend.domain.task.repository.TaskDependencyRepository;
import com.omo.backend.domain.task.repository.TaskRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskQueryService {

    private final RoadmapRepository roadmapRepository;
    private final TaskRepository taskRepository;
    private final TaskDependencyRepository taskDependencyRepository;
    private final TaskDocumentRepository taskDocumentRepository;
    private final TaskStatusCalculator taskStatusCalculator;
    private final RoadmapProgressCalculator roadmapProgressCalculator;
    private final RoadmapScheduleCalculator roadmapScheduleCalculator;

    public TaskResponseDTO.TaskListResultDTO getTasks(Long roadmapId, Long memberId) {
        validateRoadmapOwner(roadmapId, memberId);

        List<Task> tasks = taskRepository
                .findAllByRoadmap_IdOrderByDisplayOrderAscIdAsc(roadmapId);
        Map<Long, List<TaskDependency>> dependenciesByTaskId = taskDependencyRepository
                .findAllByTask_Roadmap_Id(roadmapId)
                .stream()
                .collect(Collectors.groupingBy(dependency -> dependency.getTask().getId()));
        Map<Long, List<TaskDocument>> documentsByTaskId = taskDocumentRepository
                .findAllByTask_Roadmap_Id(roadmapId)
                .stream()
                .collect(Collectors.groupingBy(TaskDocument::getTaskId));

        List<TaskResponseDTO.TaskInfoDTO> taskResponses = tasks.stream()
                .map(task -> TaskConverter.toTaskInfoDTO(
                        task,
                        taskStatusCalculator.calculate(
                                task,
                                dependenciesByTaskId.getOrDefault(
                                        task.getId(),
                                        Collections.emptyList()
                                ),
                                documentsByTaskId.getOrDefault(
                                        task.getId(),
                                        Collections.emptyList()
                                )
                        )
                ))
                .toList();

        long completedCount = tasks.stream()
                .filter(Task::isCompleted)
                .count();
        return TaskConverter.toTaskListResultDTO(
                roadmapId,
                taskResponses,
                completedCount,
                roadmapProgressCalculator.calculate(
                        documentsByTaskId.values().stream()
                                .flatMap(List::stream)
                                .toList()
                )
        );
    }

    public TaskResponseDTO.DetailResultDTO getTask(Long taskId, Long memberId) {
        Task task = taskRepository
                .findWithRoadmapAndTaskTemplateByIdAndRoadmap_Member_Id(taskId, memberId)
                .orElseThrow(() -> new TaskException(TaskErrorCode.TASK_NOT_FOUND));
        List<TaskDependency> dependencies = taskDependencyRepository.findAllByTask_Id(taskId);
        List<TaskDocument> documents = taskDocumentRepository
                .findAllByTask_IdOrderByIdAsc(taskId);
        LocalDate today = LocalDate.now();

        return TaskConverter.toDetailResultDTO(
                task,
                taskStatusCalculator.calculate(task, dependencies, documents),
                roadmapScheduleCalculator.calculateDDay(task.getDueDate(), today),
                roadmapScheduleCalculator.isOverdue(
                        task.getDueDate(),
                        today,
                        task.isCompleted()
                )
        );
    }

    private void validateRoadmapOwner(Long roadmapId, Long memberId) {
        roadmapRepository.findByIdAndMember_Id(roadmapId, memberId)
                .orElseThrow(() -> new RoadmapException(RoadmapErrorCode.ROADMAP_NOT_FOUND));
    }
}
