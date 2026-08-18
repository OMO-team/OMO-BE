package com.omo.backend.domain.purpose.controller;

import com.omo.backend.domain.purpose.dto.PurposeResponseDTO;
import com.omo.backend.domain.purpose.service.PurposeQueryService;
import com.omo.backend.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/purposes")
public class PurposeController implements PurposeControllerDocs{
    private final PurposeQueryService purposeQueryService;

    @GetMapping
    public ApiResponse<List<PurposeResponseDTO.PurposeInfo>> getPurposes() {
        return ApiResponse.onSuccess(purposeQueryService.getPurposes());
    }
}
