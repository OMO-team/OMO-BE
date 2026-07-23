package com.omo.backend.domain.document.service;

import com.omo.backend.domain.document.converter.TaskDocumentConverter;
import com.omo.backend.domain.document.dto.TaskDocumentResponseDTO;
import com.omo.backend.domain.document.entity.TaskDocument;
import com.omo.backend.domain.document.repository.TaskDocumentRepository;
import com.omo.backend.domain.task.exception.TaskErrorCode;
import com.omo.backend.domain.task.exception.TaskException;
import com.omo.backend.domain.task.repository.TaskRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskDocumentQueryService {

    private final TaskDocumentRepository taskDocumentRepository;
    private final TaskRepository taskRepository;

    public TaskDocumentResponseDTO.DocumentListResultDTO getTaskDocuments(
            Long taskId,
            Long memberId
    ) {
        validateTaskOwner(taskId, memberId);

        List<TaskDocument> taskDocuments =
                taskDocumentRepository.findAllByTask_IdOrderByIdAsc(taskId);

        return TaskDocumentConverter.toDocumentListResultDTO(
                taskId,
                taskDocuments
        );
    }

    private void validateTaskOwner(Long taskId, Long memberId) {
        taskRepository.findByIdAndRoadmap_Member_Id(taskId, memberId)
                .orElseThrow(() -> new TaskException(TaskErrorCode.TASK_NOT_FOUND));
    }
}
