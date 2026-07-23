package com.omo.backend.domain.city.service;

import com.omo.backend.domain.city.converter.CityConverter;
import com.omo.backend.domain.city.dto.CityRequestDTO;
import com.omo.backend.domain.city.dto.CityResponseDTO;
import com.omo.backend.domain.city.entity.City;
import com.omo.backend.domain.city.exception.CityErrorCode;
import com.omo.backend.domain.city.exception.CityException;
import com.omo.backend.domain.city.repository.CityRepository;
import com.omo.backend.domain.city.specification.CitySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CityQueryService {

    private final CityRepository cityRepository;

    // 필터 조건으로 도시 목록 조회
    public CityResponseDTO.CityListResult getCities(CityRequestDTO.CityFilterRequest request) {
        Specification<City> spec = Specification.where(CitySpecification.isNotDeleted())
                .and(CitySpecification.hasKeyword(request.keyword()))
                .and(CitySpecification.hasPurpose(request.purposeType()))
                .and(CitySpecification.hasMaxCost(request.maxMonthlyCost()))
                .and(CitySpecification.hasMinSafety(request.minSafetyScore()))
                .and(CitySpecification.hasHousingDifficulty(request.housingDifficulty()))
                .and(CitySpecification.hasVisaDifficulty(request.visaDifficulty()))
                .and(CitySpecification.hasCountry(request.countryCode()));

        List<City> cities = cityRepository.findAll(spec);
        return CityConverter.toCityListResult(cities);
    }

    // 단순 키워드 검색
    public CityResponseDTO.CitySearchResultDTO searchCities(String keyword){
        if (keyword == null || keyword.isBlank()) {
            throw new CityException(CityErrorCode.KEYWORD_REQUIRED);
        }
        String trimmedKeyword = keyword.trim();

        Specification<City> spec = Specification
                .where(CitySpecification.isNotDeleted())
                .and(CitySpecification.hasKeyword(trimmedKeyword));

        List<City> cities = cityRepository.findAll(spec);
        return CityConverter.toCitySearchResultDTO(trimmedKeyword, cities);
    }
}
