package com.omo.backend.domain.city.controller;

import com.omo.backend.domain.city.dto.CityResponseDTO;
import com.omo.backend.global.apiPayload.ApiResponse;
import com.omo.backend.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "City", description = "도시 API")
public interface CityControllerDocs {

    @Operation(
            summary = "도시 검색 및 필터 적용",
            description = "키워드 검색 + 필터 조건(월 생활비, 점수, 난이도 등)을 적용하여 도시 목록을 조회합니다. 모든 쿼리 파라미터는 선택 사항 입니다."
    )
    @Parameters({
            @Parameter(name = "keyword", description = "검색 키워드 (도시명, 국가명, 설명, 최대 50자)", required = false, example = "베를린"),
            @Parameter(name = "purposeType", description = "목적 (WORKING_HOLIDAY / EXCHANGE_STUDENT / INTERNSHIP)"),
            @Parameter(name = "countryCodes", description = "국가 코드 목록, 복수 선택 가능 (예: AU, JP) — ?countryCodes=AU&countryCodes=JP"),
            @Parameter(name = "maxMonthlyCost", description = "최대 월 생활비 (만원 단위, 예: 200)"),
            @Parameter(name = "minSafetyScore", description = "최소 치안 점수 (예: 4.0)"),
            @Parameter(name = "housingDifficulty", description = "숙소 난이도 (EASY / NORMAL / HARD)"),
            @Parameter(name = "visaDifficulty", description = "비자 난이도 (EASY / NORMAL / HARD)"),
            @Parameter(name = "stayDuration", description = "체류 기간(SHORT / MEDIUM / LONG / VERY_LONG)"),
            @Parameter(name = "continent", description = "대륙 (Asia / Europe / North America / South America / Oceania / Africa)")

    })
    ApiResponse<CityResponseDTO.CityListResult> getCities(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String purposeType,
            @RequestParam(required = false) List<String> countryCodes,
            @RequestParam(required = false) String maxMonthlyCost,
            @RequestParam(required = false) String minSafetyScore,
            @RequestParam(required = false) String housingDifficulty,
            @RequestParam(required = false) String visaDifficulty,
            @RequestParam(required = false) String stayDuration,
            @RequestParam(required = false) String continent
    );
}
