package com.omo.backend.domain.aisearch.service;

import com.omo.backend.domain.aisearch.dto.AiSearchResponseDTO;

import java.util.List;
import java.util.Map;

public class GeminiSchemas {

    public static Map<String, Object> get(Class<?> type) {
        if (type == AiSearchResponseDTO.ParsedConditions.class) return PARSED_CONDITIONS;
        if (type == AiSearchResponseDTO.AiRawResult.class) return AI_RAW_RESULT;
        throw new IllegalArgumentException("스키마 미정의 타입: " + type.getSimpleName());
    }

    private static Map<String, Object> str() { return Map.of("type", "string"); }
    private static Map<String, Object> bool() { return Map.of("type", "boolean"); }
    private static Map<String, Object> numInt() { return Map.of("type", "integer"); }
    private static Map<String, Object> array(Map<String, Object> items) {
        return Map.of("type", "array", "items", items);
    }
    private static Map<String, Object> enumeration(List<String> values) {
        return Map.of("type", "string", "enum", values);
    }

    private static final Map<String, Object> PARSED_CONDITIONS = Map.of(
            "type", "object",
            "properties", Map.ofEntries(
                    Map.entry("requireHighSafety", bool()),
                    Map.entry("requireEasyVisa", bool()),
                    Map.entry("requireGoodHousing", bool()),
                    Map.entry("requireGoodInfra", bool()),
                    Map.entry("requireEnglishOnly", bool()),
                    Map.entry("maxBudgetKrw", numInt()),
                    Map.entry("mentionedCountry", str()),
                    Map.entry("mentionedPurpose", str())
            )
    );

    private static final Map<String, Object> AI_RAW_RESULT = Map.of(
            "type", "object",
            "properties", Map.ofEntries(
                    Map.entry("thinkingTime", numInt()),
                    Map.entry("summary", str()),
                    Map.entry("extractedTags", array(str())),
                    Map.entry("recommendedCityIds", array(numInt())),
                    Map.entry("primaryConditionType", enumeration(
                            List.of("SAFETY", "BUDGET", "LANGUAGE", "VISA", "HOUSING", "INFRA"))),
                    Map.entry("activePurpose", str()),
                    Map.entry("selectedCountry", str()),
                    Map.entry("isEmptyResult", bool())
            ),
            "required", List.of("summary", "extractedTags", "recommendedCityIds", "primaryConditionType", "isEmptyResult")
    );
}