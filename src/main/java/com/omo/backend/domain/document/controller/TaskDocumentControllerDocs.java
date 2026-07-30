package com.omo.backend.domain.document.controller;

import com.omo.backend.domain.document.dto.TaskDocumentRequestDTO;
import com.omo.backend.domain.document.dto.TaskDocumentResponseDTO;
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

@Tag(name = "Task Document", description = "로드맵 세부 작업 필요 서류 API")
public interface TaskDocumentControllerDocs {

    @Operation(
            summary = "작업 서류 완료 체크 상태 변경",
            description = "본인 로드맵의 작업 서류 체크 상태를 변경하고, 모든 서류가 체크되면 태스크를 완료 처리합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "작업 서류 완료 체크 상태 변경 성공",
            content = @Content(schema = @Schema(
                    implementation = TaskDocumentResponseDTO.UpdateCheckResultDTO.class
            ))
    )
    ApiResponse<TaskDocumentResponseDTO.UpdateCheckResultDTO> updateCheckStatus(
            @Parameter(description = "작업 서류 ID", example = "1", required = true)
            @Positive(message = "작업 서류 ID는 양수여야 합니다.")
            @PathVariable Long taskDocumentId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "변경할 완료 체크 상태",
                    required = true
            )
            @Valid
            @RequestBody TaskDocumentRequestDTO.UpdateCheckDTO request
    );
}
