package com.omo.backend.domain.roadmap.controller;

import com.omo.backend.domain.roadmap.dto.RoadmapRequestDTO;
import com.omo.backend.domain.roadmap.dto.RoadmapResponseDTO;
import com.omo.backend.domain.roadmap.service.RoadmapCreationService;
import com.omo.backend.global.apiPayload.ApiResponse;
import com.omo.backend.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roadmaps")
public class RoadmapController implements RoadmapControllerDocs {

    private final RoadmapCreationService roadmapCreationService;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RoadmapResponseDTO.CreateResultDTO> createRoadmap(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody RoadmapRequestDTO.CreateDTO request
    ) {
        RoadmapResponseDTO.CreateResultDTO result = roadmapCreationService.create(
                userDetails.getMemberId(),
                request
        );
        return ApiResponse.created(result);
    }
}
