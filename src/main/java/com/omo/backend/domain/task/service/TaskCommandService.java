package com.omo.backend.domain.task.service;

import com.omo.backend.domain.document.entity.TaskDocument;
import com.omo.backend.domain.document.repository.TaskDocumentRepository;
import com.omo.backend.domain.task.converter.TaskConverter;
import com.omo.backend.domain.task.dto.TaskResponseDTO;
import com.omo.backend.domain.task.entity.Task;
import com.omo.backend.domain.task.entity.TaskDependency;
import com.omo.backend.domain.task.enums.TaskStatus;
import com.omo.backend.domain.task.exception.TaskErrorCode;
import com.omo.backend.domain.task.exception.TaskException;
import com.omo.backend.domain.task.repository.TaskDependencyRepository;
import com.omo.backend.domain.task.repository.TaskRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskCommandService {

    private final TaskRepository taskRepository;
    private final TaskDependencyRepository taskDependencyRepository;
    private final TaskDocumentRepository taskDocumentRepository;
    private final TaskStatusCalculator taskStatusCalculator;

    public TaskResponseDTO.CompleteResultDTO complete(Long taskId, Long memberId) {
        Task task = taskRepository.findByIdAndRoadmap_Member_Id(taskId, memberId)
                .orElseThrow(() -> new TaskException(TaskErrorCode.TASK_NOT_FOUND));
        List<TaskDependency> dependencies = taskDependencyRepository.findAllByTask_Id(taskId);
        List<TaskDocument> documents = taskDocumentRepository
                .findAllByTask_IdOrderByIdAsc(taskId);
        TaskStatus currentStatus = taskStatusCalculator.calculate(task, dependencies, documents);

        if (currentStatus == TaskStatus.LOCKED) {
            throw new TaskException(TaskErrorCode.TASK_LOCKED);
        }
        if (!documents.isEmpty()) {
            throw new TaskException(TaskErrorCode.TASK_HAS_DOCUMENTS);
        }
        if (task.isCompleted()) {
            throw new TaskException(TaskErrorCode.TASK_ALREADY_COMPLETED);
        }

        task.complete();
        return TaskConverter.toCompleteResultDTO(task, TaskStatus.COMPLETED);
    }
}
