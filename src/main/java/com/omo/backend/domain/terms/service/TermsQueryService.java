package com.omo.backend.domain.terms.service;

import com.omo.backend.domain.terms.converter.TermsConverter;
import com.omo.backend.domain.terms.dto.TermsResponseDTO;
import com.omo.backend.domain.terms.entity.Terms;
import com.omo.backend.domain.terms.repository.TermsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermsQueryService {

    private final TermsRepository termsRepository;

    // 약관 목록 조회
    public TermsResponseDTO.TermsListDTO getTermsList() {
        List<Terms> termsList = termsRepository.findAllByDeletedAtIsNullOrderByIdAsc();
        return TermsConverter.toTermsListDTO(termsList);
    }
}
