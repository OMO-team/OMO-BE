package com.omo.backend.domain.aisearch.converter;

import com.omo.backend.domain.aisearch.dto.AiSearchResponseDTO;
import com.omo.backend.domain.aisearch.dto.RecommendPromptChipResponseDTO;
import com.omo.backend.domain.aisearch.entity.RecommendPromptChip;
import com.omo.backend.domain.aisearch.enums.TaskStatus;
import com.omo.backend.domain.city.dto.CityResponseDTO;
import com.omo.backend.domain.report.dto.ReportResponseDTO;

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

    // AI 분석 - 진행 중(PROCESSING) 응답 DTO 변환
    public static AiSearchResponseDTO.BriefingStatusResult toProcessingStatusResult() {
        return AiSearchResponseDTO.BriefingStatusResult.builder()
                .status(TaskStatus.PROCESSING)
                .build();
    }

    // AI 분석 - 분석 완료(COMPLETED) 응답 DTO 변환
    public static AiSearchResponseDTO.BriefingStatusResult toCompletedStatusResult(
            Boolean isRefine,
            String activePurpose,
            String selectedCountry,
            AiSearchResponseDTO.BriefingData briefingData
    ) {
        return AiSearchResponseDTO.BriefingStatusResult.builder()
                .status(TaskStatus.COMPLETED)
                .isRefine(isRefine)
                .activePurpose(activePurpose)
                .selectedCountry(selectedCountry)
                .briefingData(briefingData)
                .build();
    }

    // AI 분석 - 결과 0건일 때(COMPLETED + empty) 응답 DTO 변환
    public static AiSearchResponseDTO.BriefingStatusResult toEmptyStatusResult(
            Boolean isRefine,
            String activePurpose,
            String selectedCountry,
            String emptyResultMessage,
            List<AiSearchResponseDTO.SuggestedRelaxation> suggestedRelaxations
    ) {
        return AiSearchResponseDTO.BriefingStatusResult.builder()
                .status(TaskStatus.COMPLETED)
                .isRefine(isRefine)
                .activePurpose(activePurpose)
                .selectedCountry(selectedCountry)
                .emptyResultMessage(emptyResultMessage)
                .suggestedRelaxations(suggestedRelaxations)
                .build();
    }

    public static AiSearchResponseDTO.BriefingData toBriefingData (
            Integer thinkingTime,
            String summary,
            List<String> extractedTags,
            List<CityResponseDTO.CitySummary> recommendedCities,
            List<ReportResponseDTO.ResourceDTO> resources
    ) {
        return AiSearchResponseDTO.BriefingData.of(thinkingTime, summary, extractedTags, recommendedCities, resources);
    }
}
