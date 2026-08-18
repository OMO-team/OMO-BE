package com.omo.backend.domain.city.controller;

import com.omo.backend.domain.city.dto.CityRequestDTO;
import com.omo.backend.domain.city.dto.CityResponseDTO;
import com.omo.backend.domain.city.exception.CityErrorCode;
import com.omo.backend.domain.city.service.CityQueryService;
import com.omo.backend.global.apiPayload.ApiResponse;
import com.omo.backend.global.apiPayload.exception.GeneralException;
import com.omo.backend.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cities")
public class CityController implements CityControllerDocs {

    private final CityQueryService cityQueryService;

    @GetMapping
    public ApiResponse<CityResponseDTO.Pagination<CityResponseDTO.CityInfo>> getCities(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String purposeType,
            @RequestParam(required = false) List<String> countryCodes,
            @RequestParam(required = false) String maxMonthlyCost,
            @RequestParam(required = false) String minSafetyScore,
            @RequestParam(required = false) String housingDifficulty,
            @RequestParam(required = false) String visaDifficulty,
            @RequestParam(required = false) String stayDuration,
            @RequestParam(required = false) String continent,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletResponse response
    ) {
        response.setHeader("Cache-Control", "no-store");
        if (keyword != null && keyword.length() > 50) {
            throw new GeneralException(CityErrorCode.INVALID_KEYWORD_LENGTH);
        }
        // 빈 문자열("") 방어: 빈 값은 null로 처리
        Integer parsedMaxCost;
        BigDecimal parsedMinSafety;
        try {
            parsedMaxCost = (maxMonthlyCost != null && !maxMonthlyCost.isBlank())
                    ? Integer.parseInt(maxMonthlyCost) : null;
            parsedMinSafety = (minSafetyScore != null && !minSafetyScore.isBlank())
                    ? new BigDecimal(minSafetyScore) : null;
        } catch (NumberFormatException e) {
            throw new GeneralException(CityErrorCode.INVALID_NUMBER_FORMAT);
        }

        if (page < 0) throw new GeneralException(CityErrorCode.INVALID_PAGE_NUMBER);
        if (size <= 0 || size > 100) throw new GeneralException(CityErrorCode.INVALID_PAGE_SIZE);

        CityRequestDTO.CityFilterRequest request = new CityRequestDTO.CityFilterRequest(
                keyword, purposeType, countryCodes, parsedMaxCost,
                parsedMinSafety, housingDifficulty, visaDifficulty, stayDuration, continent
        );
        Long memberId = (userDetails != null) ? userDetails.getMemberId() : null;
        return ApiResponse.onSuccess(cityQueryService.getCities(request, page, size, memberId));
    }
}
