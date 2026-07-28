package com.omo.backend.domain.inquiry.dto;

import com.omo.backend.domain.inquiry.enums.InquiryStatus;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public class InquiryResponseDTO {

    // 1:1 문의 결과
    @Builder
    public record InquiryResultDTO(
            Long inquiryId,
            InquiryStatus status,
            LocalDateTime createdAt
    ) {}

    // 문의 첨부파일 업로드 URL 일괄 발급 결과
    @Builder
    public record AttachmentUploadUrlsResultDTO(
            String uploadToken,
            List<AttachmentUploadUrlDTO> uploads
    ) {}

    @Builder
    public record AttachmentUploadUrlDTO(
            String uploadUrl,
            String objectKey,
            String contentType,
            Instant expiresAt
    ) {}
}
