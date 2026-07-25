package com.omo.backend.domain.aisearch.service;

import com.omo.backend.domain.aisearch.dto.AiSearchResponseDTO;

import java.util.ArrayList;
import java.util.List;

public class RelaxationSuggester {

    public List<AiSearchResponseDTO.SuggestedRelaxation> suggest(
            AiSearchResponseDTO.ParsedConditions parsed, String originalQuery) {

        List<AiSearchResponseDTO.SuggestedRelaxation> suggestions = new ArrayList<>();

        if (parsed.maxBudgetKrw() != null) {
            int relaxedBudget = parsed.maxBudgetKrw() + 200_000;
            suggestions.add(AiSearchResponseDTO.SuggestedRelaxation.of(
                    "BUDGET",
                    "예산 조건을 20만 원만 높여보세요.",
                    originalQuery.replace(
                            String.valueOf(parsed.maxBudgetKrw() / 10000) + "만원",
                            String.valueOf(relaxedBudget / 10000) + "만원"
                    )
            ));
        }

        if (parsed.mentionedCountry() != null) {
            suggestions.add(AiSearchResponseDTO.SuggestedRelaxation.of(
                    "REGION",
                    "국가 조건을 넓혀보세요.",
                    originalQuery.replace(parsed.mentionedCountry(), "")
            ));
        }

        if (Boolean.TRUE.equals(parsed.requireHighSafety())) {
            suggestions.add(AiSearchResponseDTO.SuggestedRelaxation.of(
                    "SAFETY",
                    "치안 조건을 완화해보세요.",
                    originalQuery
            ));
        }

        // 아무 조건도 안 잡히면 일반 안내 문구로 폴백 ("제안 생성 실패 시 일반 빈 결과 문구")
        if (suggestions.isEmpty()) {
            suggestions.add(AiSearchResponseDTO.SuggestedRelaxation.of(
                    "GENERAL", "조건을 조금 더 넓게 설정해보세요.", originalQuery
            ));
        }

        return suggestions;
    }
}
