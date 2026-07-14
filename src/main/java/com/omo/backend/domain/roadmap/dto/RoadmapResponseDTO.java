package com.omo.backend.domain.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

public class RoadmapResponseDTO {

    // 로드맵 생성 결과
    @Builder
    public record CreateResultDTO(
            @Schema(description = "생성된 로드맵 ID", example = "1")
            Long roadmapId,

            @Schema(description = "생성 일시")
            LocalDateTime createdAt
    ) {}
}
