package com.omo.backend.domain.aisearch.dto;

import com.omo.backend.domain.aisearch.entity.RecommendPromptChip;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "추천 프롬프트 칩 관련 응답 DTO 묶음")
public class RecommendPromptChipResponseDTO {

    @Schema(description = "단일 추천 프롬프트 칩 응답 정보")
    public record ChipInfo(
            @Schema(description = "추천 칩 ID", example = "1")
            Long id,

            @Schema(description = "화면에 표시될 추천 칩 제목", example = "가성비 유럽 여행")
            String title
    ) {
        public static ChipInfo from(RecommendPromptChip chip) {
            return new ChipInfo(chip.getId(), chip.getTitle());
        }
    }

    @Schema(description = "추천 프롬프트 칩 목록 응답 정보")
    public record ChipList(
            @Schema(description = "추천 칩 리스트")
            List<ChipInfo> prompts
    ) {
        public static ChipList from(List<RecommendPromptChip> chips) {
            List<ChipInfo> promptResponses = chips.stream()
                    .map(ChipInfo::from)
                    .toList();

            return new ChipList(promptResponses);
        }
    }
}