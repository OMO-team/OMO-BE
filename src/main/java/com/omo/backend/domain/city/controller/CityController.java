package com.omo.backend.domain.city.controller;

import com.omo.backend.domain.city.dto.CityRequestDTO;
import com.omo.backend.domain.city.dto.CityResponseDTO;
import com.omo.backend.domain.city.service.CityQueryService;
import com.omo.backend.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
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
            @Valid @ModelAttribute CityRequestDTO.CityFilterRequest request){
        return ApiResponse.onSuccess(cityQueryService.getCities(request));
    }
}
