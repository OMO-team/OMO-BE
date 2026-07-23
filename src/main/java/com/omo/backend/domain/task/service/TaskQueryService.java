package com.omo.backend.domain.task.service;

import com.omo.backend.domain.document.entity.TaskDocument;
import com.omo.backend.domain.document.repository.TaskDocumentRepository;
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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskQueryService {

    private final TaskRepository taskRepository;
    private final TaskDependencyRepository taskDependencyRepository;
    private final TaskDocumentRepository taskDocumentRepository;
    private final TaskStatusCalculator taskStatusCalculator;
    private final RoadmapScheduleCalculator roadmapScheduleCalculator;

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
}
