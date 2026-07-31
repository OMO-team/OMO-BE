package com.omo.backend.domain.city.converter;

import com.omo.backend.domain.city.dto.CityResponseDTO;
import com.omo.backend.domain.city.entity.City;

import java.util.List;

public class CityConverter {

    // City 카드 하나
    public static CityResponseDTO.CityInfo toCityInfo(City city) {
        return CityResponseDTO.CityInfo.builder()
                .cityId(city.getCityId())
                .name(city.getName())
                .country(CityResponseDTO.CountryDTO.builder()
                        .countryId(city.getCountry().getCountryId())
                        .name(city.getCountry().getName())
                        .build())
                .continent(city.getContinent())
                .imageUrl(city.getImageUrl())
                .rating(city.getRating())
                .description(city.getDescription())
                .monthlyCost(city.getMonthlyCost())
                .safetyScore(city.getSafetyScore())
                .housingScore(city.getHousingScore())
                .visaScore(city.getVisaScore())
                .languageScore(city.getLanguageScore())
                .build();
    }

    // 필터 통합 검색
    public static CityResponseDTO.CityListResult toCityListResult(List<City> cities) {
        List<CityResponseDTO.CityInfo> cityInfoList = cities.stream()
                .map(CityConverter::toCityInfo)
                .toList();

        return CityResponseDTO.CityListResult.builder()
                .totalCount(cityInfoList.size())
                .cities(cityInfoList)
                .build();
    }
}
