package com.omo.backend.domain.budget.controller;

import com.omo.backend.domain.budget.dto.BudgetRequestDTO;
import com.omo.backend.domain.budget.dto.BudgetResponseDTO;
import com.omo.backend.domain.budget.service.BudgetCommandService;
import com.omo.backend.global.apiPayload.ApiResponse;
import com.omo.backend.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roadmaps")
@Validated
public class BudgetController implements BudgetControllerDocs {

    private final BudgetCommandService budgetCommandService;

    @Override
    @PatchMapping("/{roadmapId}/budget")
    public ApiResponse<BudgetResponseDTO.UpsertResultDTO> upsertBudget(
            @Positive(message = "로드맵 ID는 양수여야 합니다.")
            @PathVariable Long roadmapId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody BudgetRequestDTO.UpsertDTO request
    ) {
        return ApiResponse.onSuccess(budgetCommandService.upsertBudget(
                roadmapId,
                userDetails.getMemberId(),
                request
        ));
    }
}
