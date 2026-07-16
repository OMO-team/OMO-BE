package com.omo.backend.domain.country.controller;

import com.omo.backend.domain.country.dto.CountryResponseDTO;
import com.omo.backend.domain.country.service.CountryQueryService;
import com.omo.backend.domain.purpose.enums.PurposeEnum;
import com.omo.backend.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/countries")
public class CountryController implements CountryControllerDocs{
    private final CountryQueryService countryQueryService;

    @GetMapping
    public ApiResponse<CountryResponseDTO.CountryListResult> getCountries(
            @RequestParam PurposeEnum purposeType
            ){
        return ApiResponse.onSuccess(countryQueryService.getCountries(purposeType));
    }

}
