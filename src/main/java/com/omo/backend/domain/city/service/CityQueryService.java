package com.omo.backend.domain.city.service;

import com.omo.backend.domain.city.converter.CityConverter;
import com.omo.backend.domain.city.dto.CityRequestDTO;
import com.omo.backend.domain.city.dto.CityResponseDTO;
import com.omo.backend.domain.city.entity.City;
import com.omo.backend.domain.city.repository.CityRepository;
import com.omo.backend.domain.city.specification.CitySpecification;
import com.omo.backend.domain.wishlist.repository.MemberWishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CityQueryService {

    private final CityRepository cityRepository;
    private final MemberWishlistRepository memberWishlistRepository;

    public CityResponseDTO.CityListResult getCities(CityRequestDTO.CityFilterRequest request, Long memberId){

        String keyword = (request.keyword() != null) ? request.keyword().trim() : null;

        Specification<City> spec = Specification
                .where(CitySpecification.isNotDeleted())
                .and(CitySpecification.hasKeyword(keyword))
                .and(CitySpecification.hasPurpose(request.purposeType()))
                .and(CitySpecification.hasMaxCost(request.maxMonthlyCost()))
                .and(CitySpecification.hasMinSafety(request.minSafetyScore()))
                .and(CitySpecification.hasHousingDifficulty(request.housingDifficulty()))
                .and(CitySpecification.hasVisaDifficulty(request.visaDifficulty()))
                .and(CitySpecification.hasCountry(request.countryCodes()))
                .and(CitySpecification.hasStayDuration(request.stayDuration()))
                .and(CitySpecification.hasContinent(request.continent()));

        List<City> cities = cityRepository.findAll(spec);

        Set<Long> wishlistedCityIds = (memberId != null)
                ? new HashSet<>(memberWishlistRepository.findWishlistedCityIdsByMemberId(memberId))
                : new HashSet<>();

        return CityConverter.toCityListResult(cities, wishlistedCityIds);
    }
}
