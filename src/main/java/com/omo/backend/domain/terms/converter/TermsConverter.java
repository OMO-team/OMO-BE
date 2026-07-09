package com.omo.backend.domain.terms.converter;

import com.omo.backend.domain.terms.dto.TermsResponseDTO;
import com.omo.backend.domain.terms.entity.Terms;

import java.util.List;

public class TermsConverter {

    // 약관 목록 조회 DTO
    public static TermsResponseDTO.TermsDTO toTermsDTO(Terms terms) {
        return TermsResponseDTO.TermsDTO.builder()
                .id(terms.getId())
                .title(terms.getTitle())
                .content(terms.getContent())
                .type(terms.getType())
                .required(terms.getRequired())
                .version(terms.getVersion())
                .effectiveAt(terms.getEffectiveAt())
                .build();
    }

    public static TermsResponseDTO.TermsListDTO toTermsListDTO(List<Terms> termsList) {
        return TermsResponseDTO.TermsListDTO.builder()
                .terms(termsList.stream()
                        .map(TermsConverter::toTermsDTO)
                        .toList())
                .build();
    }
}
