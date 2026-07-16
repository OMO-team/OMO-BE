package com.omo.backend.domain.country.controller;

import com.omo.backend.domain.country.dto.CountryResponseDTO;
import com.omo.backend.domain.purpose.enums.PurposeEnum;
import com.omo.backend.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Country", description = "국가 API")
public interface CountryControllerDocs {

    @Operation(summary = "목적별 국가 조회", description = "선택한 목적에 맞는 국가 목록 조회")
    ApiResponse<CountryResponseDTO.CountryListResult> getCountries(
            @Parameter(description = "목적 타입 (WORKING_HOLIDAY, EXCHANGE_STUDENT, INTERNSHIP)")
            PurposeEnum purposeType
    );
}
