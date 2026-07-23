package com.omo.backend.domain.task.controller;

import com.omo.backend.domain.task.dto.TaskRequestDTO;
import com.omo.backend.domain.task.dto.TaskResponseDTO;
import com.omo.backend.global.apiPayload.ApiResponse;
import com.omo.backend.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Task", description = "로드맵 태스크 API")
public interface TaskControllerDocs {

    @Operation(
            summary = "태스크 상세 조회",
            description = "태스크 상태, 권장 완료일, D-Day와 일정 초과 여부를 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "태스크 상세 조회 성공",
            content = @Content(schema = @Schema(
                    implementation = TaskResponseDTO.DetailResultDTO.class
            ))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "태스크를 찾을 수 없거나 다른 회원의 태스크"
    )
    ApiResponse<TaskResponseDTO.DetailResultDTO> getTask(
            @Parameter(description = "태스크 ID", example = "10", required = true)
            @Positive(message = "태스크 ID는 양수여야 합니다.")
            @PathVariable Long taskId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "태스크 일정 변경",
            description = "로드맵 출국일 설정 후 태스크의 권장 완료일을 변경합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "태스크 일정 변경 성공",
            content = @Content(schema = @Schema(
                    implementation = TaskResponseDTO.UpdateScheduleResultDTO.class
            ))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "로드맵 출국일이 설정되지 않음"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "태스크를 찾을 수 없거나 다른 회원의 태스크"
    )
    ApiResponse<TaskResponseDTO.UpdateScheduleResultDTO> updateTaskSchedule(
            @Parameter(description = "태스크 ID", example = "10", required = true)
            @Positive(message = "태스크 ID는 양수여야 합니다.")
            @PathVariable Long taskId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "변경할 태스크 일정",
                    required = true
            )
            @Valid @RequestBody TaskRequestDTO.UpdateScheduleDTO request
    );

    @Operation(
            summary = "서류 없는 태스크 수동 완료",
            description = "잠금 해제되고 연결 서류가 없는 미완료 태스크를 완료합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "태스크 완료 성공",
            content = @Content(schema = @Schema(
                    implementation = TaskResponseDTO.CompleteResultDTO.class
            ))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "잠긴 태스크, 서류가 있는 태스크 또는 이미 완료된 태스크"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "태스크를 찾을 수 없음"
    )
    ApiResponse<TaskResponseDTO.CompleteResultDTO> completeTask(
            @Parameter(description = "태스크 ID", example = "10", required = true)
            @Positive(message = "태스크 ID는 양수여야 합니다.")
            @PathVariable Long taskId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
