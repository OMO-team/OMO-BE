package com.omo.backend.domain.aisearch.dto;

import com.omo.backend.domain.aisearch.entity.RecommendPromptChip;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

public class RecommendPromptChipResponseDTO {

    @Builder
    public record ChipInfo(
            @Schema(description = "추천 칩 ID", example = "1")
            Long id,

            @Schema(description = "화면에 표시될 추천 칩 제목", example = "가성비 유럽 여행")
            String title
    ) {}

    @Builder
    public record ChipList(
            @Schema(description = "추천 칩 리스트")
            List<ChipInfo> prompts
    ) {}
}