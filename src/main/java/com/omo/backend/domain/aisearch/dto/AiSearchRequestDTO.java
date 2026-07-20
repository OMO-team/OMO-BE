package com.omo.backend.domain.aisearch.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public class AiSearchRequestDTO {

    @Builder
    public record BriefingRequest(
            @Schema(description = "유저가 입력한 자연어 질의어", example = "영어만으로 생활이 가능하면서 치안이 우수한 도시를 추천해줘.")
            @NotBlank(message = "검색어를 입력해 주세요.")
            String searchQuery,

            @Schema(description = "이어묻기 여부 (첫 검색 시 false, 꼬리질문 시 true)", example = "false")
            Boolean isRefine,

            @Schema(description = "대화 세션 ID (첫 검색 시 NULL, 이어묻기 시 이전 세션 ID)", example = "12345")
            Long sessionId
    ) {}
}
