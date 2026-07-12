package com.omo.backend.domain.document.service;

import com.omo.backend.domain.document.converter.TaskDocumentConverter;
import com.omo.backend.domain.document.dto.TaskDocumentResponseDTO;
import com.omo.backend.domain.document.entity.TaskDocument;
import com.omo.backend.domain.document.repository.TaskDocumentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskDocumentQueryService {

    private final TaskDocumentRepository taskDocumentRepository;

    public TaskDocumentResponseDTO.DocumentListResultDTO getTaskDocuments(
            Long taskId
    ) {
        List<TaskDocument> taskDocuments =
                taskDocumentRepository.findAllByTaskIdOrderByIdAsc(taskId);

        return TaskDocumentConverter.toDocumentListResultDTO(
                taskId,
                taskDocuments
        );
    }
}
