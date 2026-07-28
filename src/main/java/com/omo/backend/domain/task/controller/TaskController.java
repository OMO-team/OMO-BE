package com.omo.backend.domain.task.controller;

import com.omo.backend.domain.task.dto.TaskRequestDTO;
import com.omo.backend.domain.task.dto.TaskResponseDTO;
import com.omo.backend.domain.task.service.TaskCommandService;
import com.omo.backend.domain.task.service.TaskQueryService;
import com.omo.backend.global.apiPayload.ApiResponse;
import com.omo.backend.global.security.CustomUserDetails;
import jakarta.validation.constraints.Positive;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
public class TaskController implements TaskControllerDocs {

    private final TaskQueryService taskQueryService;
    private final TaskCommandService taskCommandService;

    @Override
    @GetMapping("/tasks/{taskId}")
    public ApiResponse<TaskResponseDTO.DetailResultDTO> getTask(
            @Positive(message = "태스크 ID는 양수여야 합니다.")
            @PathVariable Long taskId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.onSuccess(taskQueryService.getTask(
                taskId,
                userDetails.getMemberId()
        ));
    }

    @Override
    @PatchMapping("/tasks/{taskId}/schedule")
    public ApiResponse<TaskResponseDTO.UpdateScheduleResultDTO> updateTaskSchedule(
            @Positive(message = "태스크 ID는 양수여야 합니다.")
            @PathVariable Long taskId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody TaskRequestDTO.UpdateScheduleDTO request
    ) {
        return ApiResponse.onSuccess(taskCommandService.updateSchedule(
                taskId,
                userDetails.getMemberId(),
                request
        ));
    }

    @Override
    @PatchMapping("/tasks/{taskId}/complete")
    public ApiResponse<TaskResponseDTO.CompleteResultDTO> completeTask(
            @Positive(message = "태스크 ID는 양수여야 합니다.")
            @PathVariable Long taskId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.onSuccess(taskCommandService.complete(
                taskId,
                userDetails.getMemberId()
        ));
    }
}
