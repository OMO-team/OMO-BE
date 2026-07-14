package com.omo.backend.domain.roadmap.controller;

import com.omo.backend.domain.roadmap.dto.RoadmapRequestDTO;
import com.omo.backend.domain.roadmap.dto.RoadmapResponseDTO;
import com.omo.backend.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Roadmap", description = "로드맵 API")
public interface RoadmapControllerDocs {

    @Operation(summary = "로드맵 생성", description = "로드맵 생성 API입니다.")
    ApiResponse<RoadmapResponseDTO.CreateResultDTO> createRoadmap(
            @Valid @RequestBody RoadmapRequestDTO.CreateDTO request
            // TODO: Security 연동 시 @AuthenticationPrincipal 통해 memberId 추출 바인딩 적용
    );
}
