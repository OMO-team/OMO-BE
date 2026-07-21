package com.omo.backend.domain.roadmap.controller;

import com.omo.backend.domain.roadmap.dto.RoadmapRequestDTO;
import com.omo.backend.domain.roadmap.dto.RoadmapResponseDTO;
import com.omo.backend.global.apiPayload.ApiResponse;
import com.omo.backend.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Roadmap", description = "로드맵 API")
public interface RoadmapControllerDocs {

    @Operation(
            summary = "로드맵 생성",
            description = "인증된 회원의 도시, 목적, 출국일에 맞는 템플릿으로 로드맵과 태스크를 생성합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "로드맵 생성 성공",
            content = @Content(schema = @Schema(
                    implementation = RoadmapResponseDTO.CreateResultDTO.class
            ))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "요청값 또는 도시와 목적 조합이 유효하지 않음"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 필요"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "회원, 도시, 목적 또는 로드맵 템플릿을 찾을 수 없음"
    )
    ApiResponse<RoadmapResponseDTO.CreateResultDTO> createRoadmap(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "로드맵 생성 정보",
                    required = true
            )
            @Valid @RequestBody RoadmapRequestDTO.CreateDTO request
    );
}
