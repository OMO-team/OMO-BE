package com.omo.backend.domain.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
            Long purposeId,

            @Schema(description = "출국일", example = "2026-12-01")
            @NotNull(message = "출국일은 필수입니다.")
            @FutureOrPresent(message = "출국일은 오늘 또는 이후여야 합니다.")
            LocalDate departureDate,

            @Schema(description = "체류 개월 수", example = "12")
            @NotNull(message = "체류 개월 수는 필수입니다.")
            @Positive(message = "체류 개월 수는 양수여야 합니다.")
            Integer stayMonths,

            @Schema(description = "로드맵 제목", example = "도쿄 워킹홀리데이 준비")
            @NotBlank(message = "로드맵 제목은 필수입니다.")
            @Size(max = 100, message = "로드맵 제목은 100자 이하여야 합니다.")
            String title
    ) {
    }
}
