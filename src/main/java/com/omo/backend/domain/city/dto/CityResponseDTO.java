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
            Long countryId,
            String countryName,
            String imageUrl,
            BigDecimal rating,
            String description,
            Integer monthlyCost,
            BigDecimal safetyScore,
            BigDecimal housingScore,
            BigDecimal visaScore,
            BigDecimal languageScore,
            BigDecimal infraScore
    ) {}

    // 도시 목록 조회 결과
    public record CityListResult(
            int totalCount,
            List<CityInfo> cities
    ) {}

    //키워드 검색 결과
    @Builder
    public record CitySearchResultDTO(
            String keyword,
            Integer totalCount,
            List<CityDTO> cities
    ){}

    //도시 1개 정보
    @Builder
    public record CityDTO(
            Long cityId,
            String name,
            CountryDTO country,
            String imageUrl,
            BigDecimal rating,
            Integer monthlyCost,
            BigDecimal safetyScore
    ){}

    //국가 정보
    @Builder
    public record CountryDTO(
            Long countryId,
            String name
    ){}
}
