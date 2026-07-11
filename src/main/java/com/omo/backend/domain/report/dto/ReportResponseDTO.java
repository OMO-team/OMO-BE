package com.omo.backend.domain.report.dto;

import com.omo.backend.domain.report.entity.SummaryCategory;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class ReportResponseDTO {

    public record CoreSummaryDTO(
            @Schema(description = "핵심정보 카테고리", example = "VISA")
            SummaryCategory category,

            @Schema(description = "제목", example = "비자 발급 절차")
            String title,

            @Schema(description = "내용", example = "워킹홀리데이 비자는...")
            String content
    ) {}

    public record StatDTO(
            @Schema(description = "스탯 종류", example = "SAFETY")
            String statType,

            @Schema(description = "값", example = "78.5")
            Double value,

            @Schema(description = "최대값", example = "100")
            Double maxValue,

            @Schema(description = "단위", example = "점")
            String unit
    ) {}

    public record ProsConsDTO(
            @Schema(description = "장점 목록")
            List<String> pros,

            @Schema(description = "단점 목록")
            List<String> cons,

            @Schema(description = "장점이 없을 때 안내 문구 표시 여부", example = "false")
            boolean prosEmpty,

            @Schema(description = "단점이 없을 때 안내 문구 표시 여부", example = "false")
            boolean consEmpty
    ) {}

    public record ResourceDTO(
            @Schema(description = "주제", example = "VISA")
            String topic,

            @Schema(description = "자료 유형", example = "OFFICIAL")
            String resourceType,

            @Schema(description = "제목", example = "호주 워킹홀리데이 비자 공식 가이드")
            String title,

            @Schema(description = "출처 (국가명 + 기관명 조합)", example = "호주 이찬형")
            String source,

            @Schema(description = "링크", example = "https://example.com")
            String url
    ) {}
}
