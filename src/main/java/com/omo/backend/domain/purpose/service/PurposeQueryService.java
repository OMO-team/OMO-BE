package com.omo.backend.domain.purpose.service;

import com.omo.backend.domain.purpose.converter.PurposeConverter;
import com.omo.backend.domain.purpose.dto.PurposeResponseDTO;
import com.omo.backend.domain.purpose.repository.PurposeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurposeQueryService {
    private final PurposeRepository purposeRepository;

    public List<PurposeResponseDTO.PurposeInfo> getPurposes(){
        return purposeRepository.findAllByOrderByPurposeIdAsc()
                .stream()
                .map(PurposeConverter::toPurposeInfo)
                .toList();

    }
}
