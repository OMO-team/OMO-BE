package com.omo.backend.domain.city.converter;

import com.omo.backend.domain.city.dto.CityResponseDTO;
import com.omo.backend.domain.city.entity.City;

import java.util.List;

public class CityConverter {

    // City 엔티티 -> 도시 카드 DTO
    public static CityResponseDTO.CityInfo toCityInfo(City city) {
        return CityResponseDTO.CityInfo.builder()
                .cityId(city.getCityId())
                .name(city.getName())
                .countryId(city.getCountry().getCountryId())
                .countryName(city.getCountry().getName())
                .imageUrl(city.getImageUrl())
                .rating(city.getRating())
                .description(city.getDescription())
                .monthlyCost(city.getMonthlyCost())
                .safetyScore(city.getSafetyScore())
                .housingScore(city.getHousingScore())
                .visaScore(city.getVisaScore())
                .languageScore(city.getLanguageScore())
                .infraScore(city.getInfraScore())
                .build();
    }

    // City 목록 -> 도시 목록 조회 결과 DTO
    public static CityResponseDTO.CityListResult toCityListResult(List<City> cities) {
        List<CityResponseDTO.CityInfo> cityInfoList = cities.stream()
                .map(CityConverter::toCityInfo)
                .toList();
        return new CityResponseDTO.CityListResult(cityInfoList.size(), cityInfoList);
    }
}
