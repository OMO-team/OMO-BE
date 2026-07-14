package com.omo.backend.domain.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class RoadmapRequestDTO {

        // 로드맵 생성 요청
        public record CreateDTO(
                @Schema(description = "도시 ID", example = "5")
                @NotNull(message = "도시 ID는 필수입니다.")
                Long cityId,

                @Schema(description = "여행 목적 ID", example = "2")
                @NotNull(message = "여행 목적 ID는 필수입니다.")
                Long purposeId,

                @Schema(description = "출발 날짜", example = "2026-08-01")
                @NotNull(message = "출발 날짜는 필수입니다.")
                LocalDate departureDate,

                @Schema(description = "체류 개월 수", example = "3")
                @NotNull(message = "체류 개월 수는 필수입니다.")
                @Positive(message = "체류 개월 수는 1개월 이상이어야 합니다.")
                Integer stayMonths
        ) {}
}
