package com.omo.backend.domain.aisearch.controller;

import com.omo.backend.domain.aisearch.dto.RecommendPromptChipResponseDTO;
import com.omo.backend.domain.aisearch.service.AiSearchService;
import com.omo.backend.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai-search")
@RequiredArgsConstructor
public class AiSearchController implements AiSearchControllerDocs {

    private final AiSearchService aiSearchService;

    @Override
    @GetMapping("/recommend-chips")
    public ApiResponse<RecommendPromptChipResponseDTO.ChipList> getRecommendPromptChips() {
        RecommendPromptChipResponseDTO.ChipList response = aiSearchService.getActiveRecommendPromptChips();
        return ApiResponse.onSuccess(response);
    }
}
