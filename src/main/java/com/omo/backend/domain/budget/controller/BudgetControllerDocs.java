package com.omo.backend.domain.budget.controller;

import com.omo.backend.domain.budget.dto.BudgetRequestDTO;
import com.omo.backend.domain.budget.dto.BudgetResponseDTO;
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

@Tag(name = "Budget", description = "로드맵 예산 API")
public interface BudgetControllerDocs {

    @Operation(
            summary = "로드맵 예산 설정 및 변경",
            description = "체류 기간을 설정합니다. 최초 호출 시 도시 비용을 복사해 예산을 생성하고, 이후 호출은 복사된 비용을 유지합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "로드맵 예산 설정 성공",
            content = @Content(schema = @Schema(
                    implementation = BudgetResponseDTO.UpsertResultDTO.class
            ))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "로드맵 ID 또는 체류 기간이 유효하지 않음"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 필요"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "로드맵을 찾을 수 없거나 다른 회원의 로드맵"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "도시의 예산 정보가 설정되지 않음"
    )
    ApiResponse<BudgetResponseDTO.UpsertResultDTO> upsertBudget(
            @Parameter(description = "로드맵 ID", example = "12", required = true)
            @Positive(message = "로드맵 ID는 양수여야 합니다.")
            @PathVariable Long roadmapId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "변경할 체류 기간",
                    required = true
            )
            @Valid @RequestBody BudgetRequestDTO.UpsertDTO request
    );
}
