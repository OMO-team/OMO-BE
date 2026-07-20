package com.omo.backend.domain.inquiry.dto;

import com.omo.backend.domain.inquiry.enums.InquiryStatus;
import lombok.Builder;

import java.time.LocalDateTime;

public class InquiryResponseDTO {

    // 1:1 문의 결과
    @Builder
    public record InquiryResultDTO(
            Long inquiryId,
            InquiryStatus status,
            LocalDateTime createdAt
    ) {}
}
