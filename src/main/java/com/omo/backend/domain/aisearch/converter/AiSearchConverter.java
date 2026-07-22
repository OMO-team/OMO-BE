package com.omo.backend.domain.aisearch.converter;

import com.omo.backend.domain.aisearch.dto.AiSearchResponseDTO;
import com.omo.backend.domain.aisearch.dto.RecommendPromptChipResponseDTO;
import com.omo.backend.domain.aisearch.entity.RecommendPromptChip;

import java.util.List;

public class AiSearchConverter {

    // 단일 칩 변환
    public static RecommendPromptChipResponseDTO.ChipInfo toChipInfo(RecommendPromptChip chip) {
        return RecommendPromptChipResponseDTO.ChipInfo.builder()
                .id(chip.getId())
                .title(chip.getTitle())
                .build();
    }

    // 칩 리스트 변환
    public static RecommendPromptChipResponseDTO.ChipList toChipList(List<RecommendPromptChip> chipList) {
        return RecommendPromptChipResponseDTO.ChipList.builder()
                .prompts(chipList.stream()
                        .map(AiSearchConverter::toChipInfo)
                        .toList())
                .build();
    }

    // AI 분석 요청 초기 응답 DTO 변환
    public static AiSearchResponseDTO.BriefingInitResult toBriefingInitResult(Long sessionId, String taskId) {
        return AiSearchResponseDTO.BriefingInitResult.builder()
                .sessionId(sessionId)
                .taskId(taskId)
                .build();
    }
}
