package com.omo.backend.domain.purpose.converter;

import com.omo.backend.domain.purpose.dto.PurposeResponseDTO;
import com.omo.backend.domain.purpose.entity.Purpose;

public class PurposeConverter {

    public static PurposeResponseDTO.PurposeInfo toPurposeInfo(Purpose purpose){
        return PurposeResponseDTO.PurposeInfo.builder()
                .purposeId(purpose.getPurposeId())
                .type(purpose.getType())
                .name(purpose.getName())
                .build();
    }
}
