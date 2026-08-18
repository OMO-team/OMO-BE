package com.omo.backend.domain.purpose.controller;

import com.omo.backend.domain.purpose.dto.PurposeResponseDTO;
import com.omo.backend.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name="Purpose", description = "목적 API")
public interface PurposeControllerDocs {

    @Operation(summary = "목적 조회", description = "탐색 홈에서 목적을 조회합니다.")
    ApiResponse<List<PurposeResponseDTO.PurposeInfo>> getPurposes();
}
