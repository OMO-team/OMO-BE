package com.omo.backend.domain.roadmap.controller;

import com.omo.backend.domain.roadmap.dto.RoadmapRequestDTO;
import com.omo.backend.domain.roadmap.dto.RoadmapResponseDTO;
import com.omo.backend.domain.roadmap.service.RoadmapCreationService;
import com.omo.backend.domain.roadmap.service.RoadmapCommandService;
import com.omo.backend.domain.roadmap.service.RoadmapQueryService;
import com.omo.backend.global.apiPayload.ApiResponse;
import com.omo.backend.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roadmaps")
@Validated
public class RoadmapController implements RoadmapControllerDocs {

    private final RoadmapCreationService roadmapCreationService;
    private final RoadmapQueryService roadmapQueryService;
    private final RoadmapCommandService roadmapCommandService;

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

    @Override
    @GetMapping
    public ApiResponse<List<RoadmapResponseDTO.ListItemDTO>> getRoadmaps(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.onSuccess(
                roadmapQueryService.getRoadmaps(userDetails.getMemberId())
        );
    }

    @Override
    @GetMapping("/{roadmapId}")
    public ApiResponse<RoadmapResponseDTO.DetailResultDTO> getRoadmap(
            @Positive(message = "로드맵 ID는 양수여야 합니다.")
            @PathVariable Long roadmapId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.onSuccess(roadmapQueryService.getRoadmap(
                roadmapId,
                userDetails.getMemberId()
        ));
    }

    @Override
    @PatchMapping("/{roadmapId}/schedule")
    public ApiResponse<RoadmapResponseDTO.UpdateScheduleResultDTO> updateSchedule(
            @Positive(message = "로드맵 ID는 양수여야 합니다.")
            @PathVariable Long roadmapId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody RoadmapRequestDTO.UpdateScheduleDTO request
    ) {
        return ApiResponse.onSuccess(roadmapCommandService.updateSchedule(
                roadmapId,
                userDetails.getMemberId(),
                request
        ));
    }

    @Override
    @DeleteMapping("/{roadmapId}")
    public ApiResponse<Void> deleteRoadmap(
            @Positive(message = "로드맵 ID는 양수여야 합니다.")
            @PathVariable Long roadmapId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        roadmapCommandService.deleteRoadmap(roadmapId, userDetails.getMemberId());
        return ApiResponse.onSuccess(null);
    }
}
