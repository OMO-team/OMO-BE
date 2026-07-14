package com.omo.backend.domain.roadmap.service;

import com.omo.backend.domain.member.entity.Member;
import com.omo.backend.domain.member.exception.MemberException;
import com.omo.backend.domain.member.repository.MemberRepository;
import com.omo.backend.domain.roadmap.converter.RoadmapConverter;
import com.omo.backend.domain.roadmap.dto.RoadmapRequestDTO;
import com.omo.backend.domain.roadmap.dto.RoadmapResponseDTO;
import com.omo.backend.domain.roadmap.entity.Roadmap;
import com.omo.backend.domain.roadmap.exception.RoadmapErrorCode;
import com.omo.backend.domain.roadmap.exception.RoadmapException;
import com.omo.backend.domain.roadmap.repository.RoadmapRepository;
import com.omo.backend.global.apiPayload.code.GeneralErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public RoadmapResponseDTO.CreateResultDTO createRoadmap(Long memberId, RoadmapRequestDTO.CreateDTO request) {
        validateRoadmapData(request.departureDate(), request.stayMonths());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(GeneralErrorCode.NOT_FOUND));

        Roadmap roadmap = RoadmapConverter.toRoadmap(request, member);

        Roadmap savedRoadmap = roadmapRepository.save(roadmap);

        return RoadmapConverter.toCreateResultDTO(savedRoadmap);
    }

    private void validateRoadmapData(LocalDate departureDate, Integer stayMonths) {
        if (stayMonths != null && stayMonths <= 0) {
            throw new RoadmapException(RoadmapErrorCode.INVALID_STAY_MONTHS);
        }
        
        if (departureDate != null && departureDate.isBefore(LocalDate.now())) {
            throw new RoadmapException(RoadmapErrorCode.INVALID_DEPARTURE_DATE);
        }
    }
}
