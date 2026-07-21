package com.omo.backend.domain.document.service;

import com.omo.backend.domain.document.converter.TaskDocumentConverter;
import com.omo.backend.domain.document.dto.TaskDocumentRequestDTO;
import com.omo.backend.domain.document.dto.TaskDocumentResponseDTO;
import com.omo.backend.domain.document.entity.TaskDocument;
import com.omo.backend.domain.document.exception.DocumentErrorCode;
import com.omo.backend.domain.document.exception.DocumentException;
import com.omo.backend.domain.document.repository.TaskDocumentRepository;
import com.omo.backend.domain.task.entity.Task;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskDocumentCommandService {

    private final TaskDocumentRepository taskDocumentRepository;

    public TaskDocumentResponseDTO.UpdateCheckResultDTO updateCheckStatus(
            Long taskDocumentId,
            TaskDocumentRequestDTO.UpdateCheckDTO request
    ) {
        TaskDocument taskDocument = findTaskDocument(taskDocumentId);

        taskDocument.updateChecked(request.checked());

        List<TaskDocument> taskDocuments =
                taskDocumentRepository.findAllByTask_IdOrderByIdAsc(
                        taskDocument.getTaskId()
                );
        synchronizeTaskCompletion(taskDocument.getTask(), taskDocuments);

        return TaskDocumentConverter.toUpdateCheckResultDTO(
                taskDocument,
                taskDocuments
        );
    }

    private TaskDocument findTaskDocument(Long taskDocumentId) {
        return taskDocumentRepository.findById(taskDocumentId)
                .orElseThrow(() -> new DocumentException(
                        DocumentErrorCode.TASK_DOCUMENT_NOT_FOUND
                ));
    }

    private void synchronizeTaskCompletion(
            Task task,
            List<TaskDocument> taskDocuments
    ) {
        boolean allChecked = !taskDocuments.isEmpty()
                && taskDocuments.stream()
                .allMatch(document -> Boolean.TRUE.equals(document.getChecked()));

        if (allChecked) {
            task.complete();
            return;
        }
        task.uncomplete();
    }
}
