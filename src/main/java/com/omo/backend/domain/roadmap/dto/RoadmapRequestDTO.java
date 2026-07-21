package com.omo.backend.domain.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class RoadmapRequestDTO {

    private RoadmapRequestDTO() {
    }

    public record CreateDTO(
            @Schema(description = "도시 ID", example = "5")
            @NotNull(message = "도시 ID는 필수입니다.")
            @Positive(message = "도시 ID는 양수여야 합니다.")
            Long cityId,

            @Schema(description = "목적 ID", example = "2")
            @NotNull(message = "목적 ID는 필수입니다.")
            @Positive(message = "목적 ID는 양수여야 합니다.")
            Long purposeId
    ) {
    }
}
