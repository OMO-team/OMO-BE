package com.omo.backend.domain.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

public class BudgetResponseDTO {

    private BudgetResponseDTO() {
    }

    @Builder
    public record UpsertResultDTO(
            @Schema(description = "로드맵 ID", example = "12")
            Long roadmapId,

            @Schema(description = "체류 기간(개월)", example = "6")
            Integer stayMonths,

            @Schema(description = "초기 정착비", example = "4000000")
            Long initialSettlementCost,

            @Schema(description = "월 생활비", example = "2400000")
            Long monthlyCost,

            @Schema(description = "총예산", example = "18400000")
            Long totalCost
    ) {
    }
}
