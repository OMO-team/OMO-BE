package com.omo.backend.domain.city.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.constraints.Size;

public class CityRequestDTO {

    // 필터 조회
    public record CityFilterRequest(

            // keyword는 선택사항. keyword 검색 시 프론트에서 필수로 값을 보장해야 함
            @Schema(description = "검색어 (도시명, 국가명, 설명)", example = "베를린")
            @Size(max = 50, message = "키워드는 50자 이하이어야 합니다.")
            String keyword,

            @Schema(description = "목적 탭", example = "WORKING_HOLIDAY",
                    allowableValues = {"WORKING_HOLIDAY", "EXCHANGE_STUDENT", "INTERNSHIP"})
            String purposeType,

            @Schema(description = "국가 코드 목록 (복수 선택 가능)", example = "[\"DE\", \"AU\"]")
            List<String> countryCodes,

            @Schema(description = "최대 월 생활비 (만원 단위)", example = "200")
            Integer maxMonthlyCost,

            @Schema(description = "최소 치안 점수", example = "4.0")
            BigDecimal minSafetyScore,

            @Schema(description = "숙소 난이도", example = "EASY",
                    allowableValues = {"EASY", "NORMAL", "HARD"})
            String housingDifficulty,

            @Schema(description = "비자 난이도", example = "NORMAL",
                    allowableValues = {"EASY", "NORMAL", "HARD"})
            String visaDifficulty,

            @Schema(description = "체류 기간", example = "SHORT",
                    allowableValues = {"SHORT", "MEDIUM", "LONG", "VERY_LONG"})
            String stayDuration,

            @Schema(description = "대륙", example = "Europe",
                    allowableValues = {"Asia", "Europe", "North America", "South America", "Oceania", "Africa"})
            String continent

    ) {}
}
