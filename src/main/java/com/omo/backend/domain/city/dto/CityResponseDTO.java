package com.omo.backend.domain.city.dto;

import com.omo.backend.domain.city.entity.City;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

public class CityResponseDTO {

    // AI 검색 브리핑 추천 도시용 DTO (
    @Builder
    public record CitySummary(
            @Schema(description = "도시 ID", example = "1")
            Long cityId,

            @Schema(description = "도시 이름", example = "Sliema")
            String cityName,

            @Schema(description = "국가 이름", example = "Malta")
            String countryName,

            @Schema(description = "도시 이미지 URL", example = "https://...")
            String imageUrl,

            @Schema(description = "전체 평점", example = "4.5")
            BigDecimal rating,

            @Schema(description = "월 예상 비용", example = "1800000")
            Integer monthlyCost,

            @Schema(description = "치안 점수", example = "4.8")
            BigDecimal safetyScore,

            @Schema(description = "비자 점수", example = "4.0")
            BigDecimal visaScore,

            @Schema(description = "주거 점수", example = "3.5")
            BigDecimal housingScore,

            @Schema(description = "언어 점수 (4.0 이상이면 영어권)", example = "4.5")
            BigDecimal languageScore
    ) {
        public static CitySummary from(City city) {
            return CitySummary.builder()
                    .cityId(city.getCityId())
                    .cityName(city.getName())
                    .countryName(city.getCountry().getName())
                    .imageUrl(city.getImageUrl())
                    .rating(city.getRating())
                    .monthlyCost(city.getMonthlyCost())
                    .safetyScore(city.getSafetyScore())
                    .visaScore(city.getVisaScore())
                    .housingScore(city.getHousingScore())
                    .languageScore(city.getLanguageScore())
                    .build();
        }
    }

    // 도시 카드 단건 정보
    @Builder
    public record CityInfo(
            Long cityId,
            String name,
            CountryDTO country,
            String continent,
            String imageUrl,
            BigDecimal rating,
            String description,
            Integer monthlyCost,
            BigDecimal safetyScore,
            BigDecimal housingScore,
            BigDecimal visaScore,
            BigDecimal languageScore,
            @Schema(description = "인터넷/인프라 점수", example = "4.7")
            BigDecimal internetScore,
            @Schema(description = "권장 체류 기간", example = "SHORT",
                    allowableValues = {"SHORT", "MEDIUM", "LONG", "VERY_LONG"})
            String stayDuration,
            @Schema(description = "로그인한 사용자의 위시리스트 여부 (비로그인 시 false)", example = "true")
            boolean isWishlisted
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

    @Builder
    public record WishlistCityInfo(
            Long cityId,
            String name,
            CountryDTO country,
            String continent,
            String imageUrl,
            BigDecimal rating,
            String description,
            Integer monthlyCost,
            BigDecimal safetyScore,
            BigDecimal housingScore,
            BigDecimal visaScore,
            BigDecimal languageScore,
            @Schema(description = "인터넷/인프라 점수", example = "4.7")
            BigDecimal internetScore,
            @Schema(description = "권장 체류 기간", example = "SHORT",
                    allowableValues = {"SHORT", "MEDIUM", "LONG", "VERY_LONG"})
            String stayDuration,
            @Schema(description = "로그인한 사용자의 위시리스트 여부", example = "true")
            boolean isWishlisted,
            @Schema(description = "도시의 목적 ID", example = "3")
            Long purposeId,
            @Schema(description = "도시의 목적 이름", example = "해외 인턴십")
            String purposeName
    ) {}

    @Builder
    public record WishlistCityListResult(
            int totalCount,
            List<WishlistCityInfo> cities
    ) {}
}
