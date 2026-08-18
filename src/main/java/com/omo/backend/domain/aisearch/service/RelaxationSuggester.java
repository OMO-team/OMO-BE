package com.omo.backend.domain.aisearch.service;

import com.omo.backend.domain.aisearch.dto.AiSearchResponseDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RelaxationSuggester {

    public List<AiSearchResponseDTO.SuggestedRelaxation> suggest(
            AiSearchResponseDTO.ParsedConditions parsed, String originalQuery) {

        List<AiSearchResponseDTO.SuggestedRelaxation> suggestions = new ArrayList<>();

        if (parsed.maxBudgetKrw() != null) {
            int relaxedBudget = parsed.maxBudgetKrw() + 30;
            var relaxedConditions = withMaxBudget(parsed, relaxedBudget);
            suggestions.add(AiSearchResponseDTO.SuggestedRelaxation.of(
                    "BUDGET", "예산 조건을 30만 원만 높여보세요.",
                    buildQueryFromConditions(relaxedConditions)
            ));
        }

        if (Boolean.TRUE.equals(parsed.requireHighSafety())) {
            var relaxedConditions = withHighSafety(parsed, null); // 조건 자체를 제거
            suggestions.add(AiSearchResponseDTO.SuggestedRelaxation.of(
                    "SAFETY", "치안 조건을 완화해보세요.",
                    buildQueryFromConditions(relaxedConditions)
            ));
        }

        if (parsed.mentionedCountry() != null) {
            var relaxedConditions = withCountry(parsed, null);
            suggestions.add(AiSearchResponseDTO.SuggestedRelaxation.of(
                    "REGION", "국가 조건을 넓혀보세요.",
                    buildQueryFromConditions(relaxedConditions)
            ));
        }

        if (suggestions.isEmpty()) {
            suggestions.add(AiSearchResponseDTO.SuggestedRelaxation.of(
                    "GENERAL", "조건을 조금 더 넓게 설정해보세요.", originalQuery
            ));
        }

        return suggestions;
    }

    // 누적 조건을 자연어 문장으로 조립
    private String buildQueryFromConditions(AiSearchResponseDTO.ParsedConditions c) {
        List<String> parts = new ArrayList<>();

        if (Boolean.TRUE.equals(c.requireHighSafety())) parts.add("치안이 좋고");
        if (Boolean.TRUE.equals(c.requireEnglishOnly())) parts.add("영어로 생활 가능하고");
        if (Boolean.TRUE.equals(c.requireEasyVisa())) parts.add("비자가 쉽고");
        if (Boolean.TRUE.equals(c.requireGoodHousing())) parts.add("집 구하기 쉽고");
        if (Boolean.TRUE.equals(c.requireGoodInfra())) parts.add("인프라가 좋고");
        if (c.mentionedCountry() != null) parts.add(c.mentionedCountry() + "에 있고");
        if (c.maxBudgetKrw() != null) parts.add(c.maxBudgetKrw() + "만원 이하인");

        String prefix = String.join(" ", parts);
        return (prefix.isEmpty() ? "" : prefix + " ") + "도시 추천해줘";
    }

    private AiSearchResponseDTO.ParsedConditions withMaxBudget(AiSearchResponseDTO.ParsedConditions c, Integer budget) {
        return AiSearchResponseDTO.ParsedConditions.builder()
                .requireHighSafety(c.requireHighSafety()).requireEasyVisa(c.requireEasyVisa())
                .requireGoodHousing(c.requireGoodHousing()).requireGoodInfra(c.requireGoodInfra())
                .requireEnglishOnly(c.requireEnglishOnly())
                .maxBudgetKrw(budget)
                .mentionedCountry(c.mentionedCountry()).mentionedPurpose(c.mentionedPurpose())
                .build();
    }

    private AiSearchResponseDTO.ParsedConditions withHighSafety(AiSearchResponseDTO.ParsedConditions c, Boolean value) {
        return AiSearchResponseDTO.ParsedConditions.builder()
                .requireHighSafety(value).requireEasyVisa(c.requireEasyVisa())
                .requireGoodHousing(c.requireGoodHousing()).requireGoodInfra(c.requireGoodInfra())
                .requireEnglishOnly(c.requireEnglishOnly())
                .maxBudgetKrw(c.maxBudgetKrw())
                .mentionedCountry(c.mentionedCountry()).mentionedPurpose(c.mentionedPurpose())
                .build();
    }

    private AiSearchResponseDTO.ParsedConditions withCountry(AiSearchResponseDTO.ParsedConditions c, String country) {
        return AiSearchResponseDTO.ParsedConditions.builder()
                .requireHighSafety(c.requireHighSafety()).requireEasyVisa(c.requireEasyVisa())
                .requireGoodHousing(c.requireGoodHousing()).requireGoodInfra(c.requireGoodInfra())
                .requireEnglishOnly(c.requireEnglishOnly())
                .maxBudgetKrw(c.maxBudgetKrw())
                .mentionedCountry(country).mentionedPurpose(c.mentionedPurpose())
                .build();
    }
}