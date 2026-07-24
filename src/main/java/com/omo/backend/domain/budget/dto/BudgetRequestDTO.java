package com.omo.backend.domain.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BudgetRequestDTO {

    private BudgetRequestDTO() {
    }

    public record UpsertDTO(
            @Schema(description = "체류 기간(개월)", example = "6")
            @NotNull(message = "체류 기간은 필수입니다.")
            @Min(value = 1, message = "체류 기간은 1개월 이상이어야 합니다.")
            @Max(value = 24, message = "체류 기간은 24개월 이하여야 합니다.")
            Integer stayMonths
    ) {
    }
}
