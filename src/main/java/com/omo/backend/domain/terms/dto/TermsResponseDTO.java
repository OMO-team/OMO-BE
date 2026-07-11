package com.omo.backend.domain.terms.dto;

import com.omo.backend.domain.terms.enums.TermsType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class TermsResponseDTO {

    // 약관 목록 조회
    @Builder
    public record TermsDTO(
            Long id,
            String title,
            String content,
            TermsType type,
            Boolean required,
            String version,
            LocalDateTime effectiveAt
    ) {}

    @Builder
    public record TermsListDTO(
            List<TermsDTO> terms
    ) {}
}
