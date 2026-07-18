package com.omo.backend.domain.report.controller;

import com.omo.backend.domain.report.dto.ReportRequestDTO;
import com.omo.backend.domain.report.dto.ReportResponseDTO;
import com.omo.backend.domain.report.service.ReportCommandService;
import com.omo.backend.domain.report.service.ReportQueryService;
import com.omo.backend.global.apiPayload.ApiResponse;
import com.omo.backend.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members/me/compare-items")
public class MemberCompareItemController implements MemberCompareItemControllerDocs {

    private final ReportQueryService reportQueryService;
    private final ReportCommandService reportCommandService;

    @Override
    @GetMapping
    public ApiResponse<List<ReportResponseDTO.CompareItemDTO>> getMyCompareItems(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.onSuccess(reportQueryService.getMyCompareItems(userDetails.getMemberId()));
    }

    @Override
    @PostMapping
    public ApiResponse<Void> addCompareItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ReportRequestDTO.AddCompareItemDTO request
    ) {
        reportCommandService.addCompareItem(userDetails.getMemberId(), request.cityId());
        return ApiResponse.created(null);
    }

    @Override
    @DeleteMapping("/{cityId}")
    public ApiResponse<Void> removeCompareItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long cityId
    ) {
        reportCommandService.removeCompareItem(userDetails.getMemberId(), cityId);
        return ApiResponse.noContent();
    }
}