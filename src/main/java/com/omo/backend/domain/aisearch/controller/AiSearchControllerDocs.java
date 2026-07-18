package com.omo.backend.domain.aisearch.controller;

import com.omo.backend.domain.aisearch.dto.RecommendPromptChipResponseDTO;
import com.omo.backend.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AI Search", description = "AI 검색 API")
public interface AiSearchControllerDocs {

    @Operation(summary = "추천 프롬프트 칩 목록 조회 API", description = "현재 활성화 상태(isActive = true)인 추천 검색 칩 리스트를 조회합니다.")
    ApiResponse<RecommendPromptChipResponseDTO.ChipList> getRecommendPromptChips();
}
