package com.omo.backend.domain.task.controller;

import com.omo.backend.domain.task.dto.TaskResponseDTO;
import com.omo.backend.global.apiPayload.ApiResponse;
import com.omo.backend.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Task", description = "로드맵 태스크 API")
public interface TaskControllerDocs {

    @Operation(
            summary = "로드맵 태스크 목록 조회",
            description = "선행 태스크, 완료 사실, 서류 체크 상태로 현재 상태와 진행률을 계산합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "태스크 목록 조회 성공",
            content = @Content(schema = @Schema(
                    implementation = TaskResponseDTO.TaskListResultDTO.class
            ))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "로드맵을 찾을 수 없음"
    )
    ApiResponse<TaskResponseDTO.TaskListResultDTO> getTasks(
            @Parameter(description = "로드맵 ID", example = "1", required = true)
            @Positive(message = "로드맵 ID는 양수여야 합니다.")
            @PathVariable Long roadmapId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
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
