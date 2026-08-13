package com.omo.backend.domain.country.controller;

import com.omo.backend.domain.country.dto.CountryResponseDTO;
import com.omo.backend.domain.purpose.enums.PurposeEnum;
import com.omo.backend.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Country", description = "국가 API")
public interface CountryControllerDocs {

    @Operation(summary = "국가 조회", description = "국가 목록을 조회합니다.\n\n" +
            "- purposeType을 지정하면, 해당 목적의 도시가 있는 국가만 반환합니다 (목적 탭 카드용).\n" +
            "- purposeType을 생략하면, 목적과 무관하게 도시가 있는 전체 국가를 반환합니다 (지역/대륙 필터용).")
    ApiResponse<CountryResponseDTO.CountryListResult> getCountries(
            @Parameter(description = "목적 타입 (WORKING_HOLIDAY, EXCHANGE_STUDENT, INTERNSHIP). 생략 시 목적과 무관하게 도시가 있는 전체 국가를 반환합니다.", required = false)
            PurposeEnum purposeType
    );
}
