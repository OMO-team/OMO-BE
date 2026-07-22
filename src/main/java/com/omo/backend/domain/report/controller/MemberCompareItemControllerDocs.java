package com.omo.backend.domain.report.controller;

import com.omo.backend.domain.report.dto.ReportRequestDTO;
import com.omo.backend.domain.report.dto.ReportResponseDTO;
import com.omo.backend.global.apiPayload.ApiResponse;
import com.omo.backend.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Compare Item", description = "비교함 API")
public interface MemberCompareItemControllerDocs {

    @Operation(summary = "내 비교함 목록 조회", description = "로그인한 사용자의 비교함 목록을 조회합니다.")
    ApiResponse<List<ReportResponseDTO.CompareItemDTO>> getMyCompareItems(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(summary = "비교함에 도시 담기", description = "비교함에 도시를 담습니다. 최대 3개까지 가능합니다.")
    ApiResponse<Void> addCompareItem(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ReportRequestDTO.AddCompareItemDTO request
    );

    @Operation(summary = "비교함에서 도시 삭제", description = "비교함에서 도시를 삭제합니다.")
    ApiResponse<Void> removeCompareItem(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "삭제할 도시 ID") Long cityId
    );
}