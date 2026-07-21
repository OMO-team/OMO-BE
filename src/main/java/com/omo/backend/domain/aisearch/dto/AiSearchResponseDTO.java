package com.omo.backend.domain.aisearch.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

public class AiSearchResponseDTO {

    @Builder
    public record BriefingInitResult(
            @Schema(description = "대화 세션 ID", example = "12345")
            Long sessionId,

            @Schema(description = "상태 조회용 작업 ID", example = "task_req_9988")
            String taskId
    ) {}
}
