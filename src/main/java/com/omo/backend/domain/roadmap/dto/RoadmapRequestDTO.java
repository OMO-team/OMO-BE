package com.omo.backend.domain.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

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

    public record UpdateScheduleDTO(
            @Schema(description = "출국일", example = "2026-12-01")
            @NotNull(message = "출국일은 필수입니다.")
            LocalDate departureDate,

            @Schema(description = "체류 기간(개월)", example = "12")
            @NotNull(message = "체류 기간은 필수입니다.")
            @Positive(message = "체류 기간은 양수여야 합니다.")
            Integer stayMonths
    ) {
    }
}
