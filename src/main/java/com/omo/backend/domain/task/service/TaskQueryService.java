package com.omo.backend.domain.task.service;

import com.omo.backend.domain.document.entity.TaskDocument;
import com.omo.backend.domain.document.repository.TaskDocumentRepository;
import com.omo.backend.domain.roadmap.exception.RoadmapErrorCode;
import com.omo.backend.domain.roadmap.exception.RoadmapException;
import com.omo.backend.domain.roadmap.repository.RoadmapRepository;
import com.omo.backend.domain.roadmap.service.RoadmapProgressCalculator;
import com.omo.backend.domain.task.converter.TaskConverter;
import com.omo.backend.domain.task.dto.TaskResponseDTO;
import com.omo.backend.domain.task.entity.Task;
import com.omo.backend.domain.task.entity.TaskDependency;
import com.omo.backend.domain.task.repository.TaskDependencyRepository;
import com.omo.backend.domain.task.repository.TaskRepository;
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
                roadmapProgressCalculator.calculate(tasks)
        );
    }

    private void validateRoadmapOwner(Long roadmapId, Long memberId) {
        roadmapRepository.findByIdAndMember_Id(roadmapId, memberId)
                .orElseThrow(() -> new RoadmapException(RoadmapErrorCode.ROADMAP_NOT_FOUND));
    }
}
