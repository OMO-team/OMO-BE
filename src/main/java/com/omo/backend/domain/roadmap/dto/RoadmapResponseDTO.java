package com.omo.backend.domain.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;

public class RoadmapResponseDTO {

    private RoadmapResponseDTO() {
    }

    @Builder
    public record CreateResultDTO(
            @Schema(description = "생성된 로드맵 ID", example = "1")
            Long roadmapId,

            @Schema(description = "로드맵 제목", example = "도쿄 워킹홀리데이 로드맵")
            String title,

            @Schema(description = "도시 ID", example = "5")
            Long cityId,

            @Schema(description = "목적 ID", example = "2")
            Long purposeId,

            @Schema(description = "출국일", example = "2026-12-01")
            LocalDate departureDate,

            @Schema(description = "생성된 전체 태스크 수", example = "12")
            Integer taskCount
    ) {
    }
}
