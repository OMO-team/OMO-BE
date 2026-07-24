package com.omo.backend.domain.city.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

public class CityResponseDTO {

    // 도시 카드 단건 정보
    @Builder
    public record CityInfo(
            Long cityId,
            String name,
            CountryDTO country,
            String imageUrl,
            BigDecimal rating,
            // TODO : 즐겨찾기 저장 추가
            String description,
            Integer monthlyCost,
            BigDecimal safetyScore,
            BigDecimal housingScore,
            BigDecimal visaScore,
            BigDecimal languageScore,
            BigDecimal infraScore
    ) {}

    // 필터 목록 결과
    @Builder
    public record CityListResult(
            int totalCount,
            List<CityInfo> cities
    ) {}

    //국가 정보
    @Builder
    public record CountryDTO(
            Long countryId,
            String name
    ){}
}
