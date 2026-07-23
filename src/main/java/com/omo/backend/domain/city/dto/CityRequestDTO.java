package com.omo.backend.domain.city.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public class CityRequestDTO {

    // 필터 조회
    public record CityFilterRequest(
            @Schema(description = "검색어 (도시명, 국가명, 설명)", example = "베를린")
            String keyword,

            @Schema(description = "목적 탭", example = "WORKING_HOLIDAY",
                    allowableValues = {"WORKING_HOLIDAY", "EXCHANGE_STUDENT", "INTERNSHIP"})
            String purposeType,

            @Schema(description = "국가 코드", example = "DE")
            String countryCode,

            @Schema(description = "최대 월 생활비 (만원 단위)", example = "200")
            Integer maxMonthlyCost,

            @Schema(description = "최소 치안 점수", example = "4.0")
            BigDecimal minSafetyScore,

            @Schema(description = "숙소 난이도", example = "EASY",
                    allowableValues = {"EASY", "NORMAL", "HARD"})
            String housingDifficulty,

            @Schema(description = "비자 난이도", example = "NORMAL",
                    allowableValues = {"EASY", "NORMAL", "HARD"})
            String visaDifficulty
    ) {}
}
