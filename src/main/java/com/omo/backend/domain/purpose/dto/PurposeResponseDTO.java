package com.omo.backend.domain.purpose.dto;

import com.omo.backend.domain.purpose.enums.PurposeEnum;
import lombok.Builder;

public class PurposeResponseDTO {

    @Builder
    public record PurposeInfo(
            Long purposeId,
            PurposeEnum type,
            String name
    ) {}
}
