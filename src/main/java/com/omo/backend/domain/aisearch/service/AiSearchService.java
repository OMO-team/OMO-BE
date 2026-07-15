package com.omo.backend.domain.aisearch.service;

import com.omo.backend.domain.aisearch.converter.AiSearchConverter;
import com.omo.backend.domain.aisearch.dto.RecommendPromptChipResponseDTO;
import com.omo.backend.domain.aisearch.entity.RecommendPromptChip;
import com.omo.backend.domain.aisearch.repository.RecommendPromptChipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 조회 성능 최적화를 위해 readOnly 적용!
public class AiSearchService {

    private final RecommendPromptChipRepository recommendPromptChipRepository;

    /**
     * 현재 활성화 상태(isActive = true)인 추천 프롬프트 칩 목록을 조회
     **/
    public RecommendPromptChipResponseDTO.ChipList getActiveRecommendPromptChips() {
        List<RecommendPromptChip> chips = recommendPromptChipRepository.findAllByIsActiveTrue();

        return AiSearchConverter.toChipList(chips);
    }
}
