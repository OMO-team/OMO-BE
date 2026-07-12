package com.omo.backend.domain.document.converter;

import com.omo.backend.domain.document.dto.TaskDocumentResponseDTO;
import com.omo.backend.domain.document.entity.DocumentTemplate;
import com.omo.backend.domain.document.entity.TaskDocument;
import java.util.List;

public class TaskDocumentConverter {

    private TaskDocumentConverter() {
    }

    public static TaskDocumentResponseDTO.DocumentItemDTO toDocumentItemDTO(
            TaskDocument taskDocument
    ) {
        DocumentTemplate documentTemplate = taskDocument.getDocumentTemplate();

        return TaskDocumentResponseDTO.DocumentItemDTO.builder()
                .taskDocumentId(taskDocument.getId())
                .documentName(documentTemplate.getDocumentName())
                .description(documentTemplate.getDescription())
                .ocrSupport(documentTemplate.getOcrSupport())
                .checked(taskDocument.getChecked())
                .build();
    }

    public static TaskDocumentResponseDTO.DocumentListResultDTO toDocumentListResultDTO(
            Long taskId,
            List<TaskDocument> taskDocuments
    ) {
        List<TaskDocumentResponseDTO.DocumentItemDTO> documents = taskDocuments.stream()
                .map(TaskDocumentConverter::toDocumentItemDTO)
                .toList();

        return TaskDocumentResponseDTO.DocumentListResultDTO.builder()
                .taskId(taskId)
                .completedCount(countCompleted(taskDocuments))
                .totalCount(countTotal(taskDocuments))
                .documents(documents)
                .build();
    }

    public static TaskDocumentResponseDTO.UpdateCheckResultDTO toUpdateCheckResultDTO(
            TaskDocument updatedTaskDocument,
            List<TaskDocument> taskDocuments
    ) {
        return TaskDocumentResponseDTO.UpdateCheckResultDTO.builder()
                .taskDocumentId(updatedTaskDocument.getId())
                .checked(updatedTaskDocument.getChecked())
                .completedCount(countCompleted(taskDocuments))
                .totalCount(countTotal(taskDocuments))
                .build();
    }

    private static Long countCompleted(List<TaskDocument> taskDocuments) {
        return taskDocuments.stream()
                .filter(taskDocument -> Boolean.TRUE.equals(taskDocument.getChecked()))
                .count();
    }

    private static Long countTotal(List<TaskDocument> taskDocuments) {
        return (long) taskDocuments.size();
    }
}
