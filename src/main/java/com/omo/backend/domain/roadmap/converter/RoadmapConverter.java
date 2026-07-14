package com.omo.backend.domain.roadmap.converter;

import com.omo.backend.domain.member.entity.Member;
import com.omo.backend.domain.roadmap.dto.RoadmapRequestDTO;
import com.omo.backend.domain.roadmap.dto.RoadmapResponseDTO;
import com.omo.backend.domain.roadmap.entity.Roadmap;

public final class RoadmapConverter {

    private RoadmapConverter() {}

    public static Roadmap toRoadmap(RoadmapRequestDTO.CreateDTO request, Member member) {
        return Roadmap.createRoadmap(
                member,
                request.cityId(),
                request.purposeId(),
                request.departureDate(),
                request.stayMonths()
        );
    }

    public static RoadmapResponseDTO.CreateResultDTO toCreateResultDTO(Roadmap roadmap) {
        return RoadmapResponseDTO.CreateResultDTO.builder()
                .roadmapId(roadmap.getId())
                .createdAt(roadmap.getCreatedAt()) // BaseEntity의 생성일자 매핑
                .build();
    }
}
