package com.omo.backend.domain.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

public class TaskDocumentResponseDTO {

    private TaskDocumentResponseDTO() {
    }

    @Builder
    public record DocumentItemDTO(
            @Schema(description = "작업 서류 ID", example = "1")
            Long taskDocumentId,

            @Schema(description = "서류명", example = "재직증명서")
            String documentName,

            @Schema(description = "서류 설명", example = "현재 재직 상태를 증명하는 서류입니다.")
            String description,

            @Schema(description = "OCR 지원 여부", example = "true")
            Boolean ocrSupport,

            @Schema(description = "서류 완료 체크 여부", example = "false")
            Boolean checked
    ) {
    }

    @Builder
    public record DocumentListResultDTO(
            @Schema(description = "태스크 ID", example = "10")
            Long taskId,

            @Schema(description = "완료된 서류 개수", example = "2")
            Long completedCount,

            @Schema(description = "전체 서류 개수", example = "5")
            Long totalCount,

            @Schema(description = "필요 서류 목록")
            List<DocumentItemDTO> documents
    ) {
    }

    @Builder
    public record UpdateCheckResultDTO(
            @Schema(description = "작업 서류 ID", example = "1")
            Long taskDocumentId,

            @Schema(description = "변경된 서류 완료 체크 여부", example = "true")
            Boolean checked,

            @Schema(description = "완료된 서류 개수", example = "3")
            Long completedCount,

            @Schema(description = "전체 서류 개수", example = "5")
            Long totalCount
    ) {
    }
}
