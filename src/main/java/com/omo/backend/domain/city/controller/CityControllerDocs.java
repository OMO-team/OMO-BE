package com.omo.backend.domain.city.controller;

import com.omo.backend.domain.city.dto.CityRequestDTO;
import com.omo.backend.domain.city.dto.CityResponseDTO;
import com.omo.backend.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.ModelAttribute;

@Tag(name = "City", description = "도시 API")
public interface CityControllerDocs {

    @Operation(
            summary = "필터링된 도시 목록 조회",
            description = "검색어(도시명, 국가명, 설명) 및 다양한 필터 조건(월 생활비, 점수, 난이도 등)을 적용하여 도시 목록을 조회합니다. 모든 쿼리 파라미터는 선택 사항 입니다."
    )
    @Parameters({
            @Parameter(name = "keyword", description = "검색어 (도시명, 국가명, 설명)"),
            @Parameter(name = "purposeType", description = "목적 (WORKING_HOLIDAY / EXCHANGE_STUDENT / INTERNSHIP)"),
            @Parameter(name = "countryCode", description = "국가 코드 (예: AU, JP)"),
            @Parameter(name = "maxMonthlyCost", description = "최대 월 생활비 (만원 단위, 예: 200)"),
            @Parameter(name = "minSafetyScore", description = "최소 치안 점수 (예: 4.0)"),
            @Parameter(name = "housingDifficulty", description = "숙소 난이도 (EASY / NORMAL / HARD)"),
            @Parameter(name = "visaDifficulty", description = "비자 난이도 (EASY / NORMAL / HARD)")
    })
    ApiResponse<CityResponseDTO.CityListResult> getCities(
            @ModelAttribute CityRequestDTO.CityFilterRequest request
    );
}
