package com.omo.backend.domain.country.converter;

import com.omo.backend.domain.country.dto.CountryResponseDTO;
import com.omo.backend.domain.country.entity.Country;
import com.omo.backend.domain.purpose.enums.PurposeEnum;

import java.util.List;

public class CountryConverter {

    // Country -> 국가 정보 DTO
    public static CountryResponseDTO.CountryInfo toCountryInfo(Country country){
        return CountryResponseDTO.CountryInfo.builder()
                .countryId(country.getCountryId())
                .name(country.getName())
                .code(country.getCode())
                .imageUrl(country.getImageUrl())
                .build();
    }

    // CountryInfo 리스트 -> 특정 목적에 해당하는 국가 목록 DTO
    public static CountryResponseDTO.CountryListResult toCountryListResult(
            PurposeEnum purposeType, List<CountryResponseDTO.CountryInfo> countries){
        return CountryResponseDTO.CountryListResult.builder()
                .purposeType(purposeType.name())
                .countries(countries)
                .build();

    }
}
