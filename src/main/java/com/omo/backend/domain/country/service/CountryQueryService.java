package com.omo.backend.domain.country.service;

import com.omo.backend.domain.country.converter.CountryConverter;
import com.omo.backend.domain.country.dto.CountryResponseDTO;
import com.omo.backend.domain.country.repository.CountryRepository;
import com.omo.backend.domain.purpose.enums.PurposeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CountryQueryService {
    private final CountryRepository countryRepository;

    public CountryResponseDTO.CountryListResult getCountries(PurposeEnum purposeType){
        List<CountryResponseDTO.CountryInfo> countries = countryRepository
                .findCountriesByPurposeType(purposeType)
                .stream()
                .map(CountryConverter::toCountryInfo)
                .toList();
        return CountryConverter.toCountryListResult(purposeType, countries);
    }
}
