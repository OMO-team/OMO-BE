package com.omo.backend.domain.roadmap.controller;

import com.omo.backend.domain.roadmap.dto.RoadmapRequestDTO;
import com.omo.backend.domain.roadmap.dto.RoadmapResponseDTO;
import com.omo.backend.domain.roadmap.service.RoadmapService;
import com.omo.backend.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roadmaps")
public class RoadmapController implements RoadmapControllerDocs{

    private final RoadmapService roadmapService;

    @PostMapping
    public ApiResponse<RoadmapResponseDTO.CreateResultDTO> createRoadmap(
            @Valid @RequestBody RoadmapRequestDTO.CreateDTO request
            // TODO: Security 연동 시 @AuthenticationPrincipal 통해 memberId 추출 바인딩 적용
            ) {
        // 임시 id
        Long temporaryMemberId = 1L;

        RoadmapResponseDTO.CreateResultDTO result = roadmapService.createRoadmap(temporaryMemberId, request);

        return ApiResponse.created(result);
    }
}
