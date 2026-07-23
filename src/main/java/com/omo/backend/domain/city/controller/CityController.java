package com.omo.backend.domain.city.controller;

import com.omo.backend.domain.city.dto.CityRequestDTO;
import com.omo.backend.domain.city.dto.CityResponseDTO;
import com.omo.backend.domain.city.service.CityQueryService;
import com.omo.backend.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cities")
public class CityController implements CityControllerDocs {

    private final CityQueryService cityQueryService;

    @GetMapping
    public ApiResponse<CityResponseDTO.CityListResult> getCities(
            @ModelAttribute CityRequestDTO.CityFilterRequest request){
        return ApiResponse.onSuccess(cityQueryService.getCities(request));
    }
}
