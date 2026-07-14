package com.omo.backend.domain.country.converter;

import com.omo.backend.domain.country.dto.CountryResponseDTO;
import com.omo.backend.domain.country.entity.Country;
import com.omo.backend.domain.purpose.enums.PurposeEnum;

import java.util.List;

public class CountryConverter {

    public static CountryResponseDTO.CountryInfo toCountryInfo(Country country){
        return CountryResponseDTO.CountryInfo.builder()
                .countryId(country.getCountryId())
                .name(country.getName())
                .code(country.getCode())
                .imageUrl(country.getImageUrl())
                .build();
    }

    public static CountryResponseDTO.CountryListResult toCountryListResult(
            PurposeEnum purposeType, List<CountryResponseDTO.CountryInfo> countries){
        return CountryResponseDTO.CountryListResult.builder()
                .purposeType(purposeType.name())
                .countries(countries)
                .build();

    }
}
