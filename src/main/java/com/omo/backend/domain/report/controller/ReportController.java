package com.omo.backend.domain.report.controller;

import com.omo.backend.domain.report.dto.ReportRequestDTO;
import com.omo.backend.domain.report.dto.ReportResponseDTO;
import com.omo.backend.domain.report.service.ReportQueryService;
import com.omo.backend.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cities")
public class ReportController implements ReportControllerDocs {

    private final ReportQueryService reportQueryService;

    @GetMapping("/{cityId}/core-summaries")
    public ApiResponse<List<ReportResponseDTO.CoreSummaryDTO>> getCoreSummaries(
            @PathVariable Long cityId
    ) {
        return ApiResponse.onSuccess(reportQueryService.getCoreSummaries(cityId));
    }

    @GetMapping("/{cityId}/pros-cons")
    public ApiResponse<ReportResponseDTO.ProsConsDTO> getProsCons(
            @PathVariable Long cityId
    ) {
        return ApiResponse.onSuccess(reportQueryService.getProsCons(cityId));
    }

    @GetMapping("/{cityId}/resources")
    public ApiResponse<List<ReportResponseDTO.ResourceDTO>> getResources(
            @PathVariable Long cityId,
            @RequestParam(required = false) String topic
    ) {
        return ApiResponse.onSuccess(reportQueryService.getResources(cityId, topic));
    }

    @PostMapping("/{cityId}/ai-report")
    public ApiResponse<ReportResponseDTO.AiReportDTO> getAiReport(
            @PathVariable Long cityId,
            @RequestBody ReportRequestDTO.AiReportRequestDTO request
    ) {
        return ApiResponse.onSuccess(reportQueryService.getAiReport(cityId, request.question()));
    }

    @Override
    @GetMapping("/{cityId}/stats")
    public ApiResponse<List<ReportResponseDTO.StatDTO>> getStats(
            @PathVariable Long cityId
    ) {
        return ApiResponse.onSuccess(reportQueryService.getStats(cityId));
    }
}
