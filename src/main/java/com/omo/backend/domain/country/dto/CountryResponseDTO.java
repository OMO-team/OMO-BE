package com.omo.backend.domain.country.dto;

import lombok.Builder;

import java.util.List;

public class CountryResponseDTO {

    @Builder
    public record CountryInfo(
            Long countryId,
            String name,
            String code,
            String imageUrl
    ){}

    @Builder
    public record CountryListResult(
            String purposeType,
            List<CountryInfo> countries

    ){}
}
