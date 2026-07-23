package com.omo.backend.domain.city.controller;

import com.omo.backend.domain.city.dto.CityRequestDTO;
import com.omo.backend.domain.city.dto.CityResponseDTO;
import com.omo.backend.domain.city.service.CityQueryService;
import com.omo.backend.global.apiPayload.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cities")
@Validated
public class CityController implements CityControllerDocs {

    private final CityQueryService cityQueryService;

    @GetMapping
    public ApiResponse<CityResponseDTO.CityListResult> getCities(
            @ModelAttribute CityRequestDTO.CityFilterRequest request){
        return ApiResponse.onSuccess(cityQueryService.getCities(request));
    }

    @GetMapping("/search")
    public ApiResponse<CityResponseDTO.CitySearchResultDTO> searchCities(
            @RequestParam
            @NotBlank(message = "검색어 키워드는 필수입니다.")
            @Size(min = 1, max = 20, message = "검색어는 1자 이상 20자 이하로 입력해주세요.")
            String keyword
    ){
        CityResponseDTO.CitySearchResultDTO result = cityQueryService.searchCities(keyword);
        return ApiResponse.onSuccess(result);
    }
}
