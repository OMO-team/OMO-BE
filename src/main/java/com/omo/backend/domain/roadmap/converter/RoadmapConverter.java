package com.omo.backend.domain.roadmap.converter;

import com.omo.backend.domain.roadmap.dto.RoadmapResponseDTO;
import com.omo.backend.domain.roadmap.entity.Roadmap;

public final class RoadmapConverter {

    private RoadmapConverter() {
    }

    public static RoadmapResponseDTO.CreateResultDTO toCreateResultDTO(
            Roadmap roadmap,
            int taskCount
    ) {
        return RoadmapResponseDTO.CreateResultDTO.builder()
                .roadmapId(roadmap.getId())
                .title(roadmap.getTitle())
                .cityId(roadmap.getCity().getCityId())
                .purposeId(roadmap.getPurpose().getPurposeId())
                .departureDate(roadmap.getDepartureDate())
                .taskCount(taskCount)
                .build();
    }
}
