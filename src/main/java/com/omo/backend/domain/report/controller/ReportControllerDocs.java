package com.omo.backend.domain.report.controller;

import com.omo.backend.domain.report.dto.ReportRequestDTO;
import com.omo.backend.domain.report.dto.ReportResponseDTO;
import com.omo.backend.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Report", description = "도시카드/리포트 API")
public interface ReportControllerDocs {
    @Operation(summary = "핵심정보 6대요약 조회", description = "도시의 핵심정보 6대요약을 조회합니다.")
    ApiResponse<List<ReportResponseDTO.CoreSummaryDTO>> getCoreSummaries(
            @Parameter(description = "도시 ID") Long cityId
    );

    @Operation(summary = "장단점 조회", description = "도시의 장단점을 조회합니다.")
    ApiResponse<ReportResponseDTO.ProsConsDTO> getProsCons(
            @Parameter(description = "도시 ID") Long cityId
    );

    @Operation(summary = "관련자료 조회", description = "도시의 관련자료를 topic으로 필터링해서 조회합니다.")
    ApiResponse<List<ReportResponseDTO.ResourceDTO>> getResources(
            @Parameter(description = "도시 ID") Long cityId,
            @Parameter(description = "주제 (COST/HOUSING/VISA/SAFETY)") String topic
    );

    @Operation(summary = "도시별 AI 질문 답변",
            description = "질문에 대한 AI 답변과 관련자료를 조회합니다. (LLM 연동 전 임시 응답)")
    ApiResponse<ReportResponseDTO.AiReportDTO> getAiReport(
            @Parameter(description = "도시 ID") Long cityId,
            @RequestBody(description = "질문 내용") ReportRequestDTO.AiReportRequestDTO request
    );

    @Operation(summary = "도시 수치 스탯 조회", description = "도시의 생활비/치안/주거/비자/인프라 스탯을 조회합니다.")
    ApiResponse<List<ReportResponseDTO.StatDTO>> getStats(
            @Parameter(description = "도시 ID") Long cityId
    );
}
