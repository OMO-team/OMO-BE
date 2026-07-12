package com.omo.backend.domain.document.controller;

import com.omo.backend.domain.document.dto.TaskDocumentRequestDTO;
import com.omo.backend.domain.document.dto.TaskDocumentResponseDTO;
import com.omo.backend.domain.document.service.TaskDocumentCommandService;
import com.omo.backend.domain.document.service.TaskDocumentQueryService;
import com.omo.backend.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
public class TaskDocumentController
        implements TaskDocumentControllerDocs {

    private final TaskDocumentQueryService taskDocumentQueryService;
    private final TaskDocumentCommandService taskDocumentCommandService;

    @Override
    @GetMapping("/tasks/{taskId}/documents")
    public ApiResponse<TaskDocumentResponseDTO.DocumentListResultDTO> getTaskDocuments(
            @Positive(message = "태스크 ID는 양수여야 합니다.")
            @PathVariable Long taskId
    ) {
        TaskDocumentResponseDTO.DocumentListResultDTO result =
                taskDocumentQueryService.getTaskDocuments(taskId);

        return ApiResponse.onSuccess(result);
    }

    @Override
    @PatchMapping("/task-documents/{taskDocumentId}/check")
    public ApiResponse<TaskDocumentResponseDTO.UpdateCheckResultDTO> updateCheckStatus(
            @Positive(message = "작업 서류 ID는 양수여야 합니다.")
            @PathVariable Long taskDocumentId,

            @Valid
            @RequestBody TaskDocumentRequestDTO.UpdateCheckDTO request
    ) {
        TaskDocumentResponseDTO.UpdateCheckResultDTO result =
                taskDocumentCommandService.updateCheckStatus(
                        taskDocumentId,
                        request
                );

        return ApiResponse.onSuccess(result);
    }
}
