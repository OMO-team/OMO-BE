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
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Roadmap", description = "로드맵 API")
public interface RoadmapControllerDocs {

    @Operation(
            summary = "로드맵 생성",
            description = "AI 탐색 리포트의 도시와 목적으로 로드맵을 생성합니다. 제목은 템플릿 이름을 사용하며 일정은 생성 후 설정합니다.",
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
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "도시와 목적에 대응하는 템플릿이 여러 개여서 하나로 결정할 수 없음"
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

    @Operation(
            summary = "내 로드맵 목록 조회",
            description = "로그인한 회원의 로드맵을 페이지네이션 없는 배열로 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "로드맵 목록 조회 성공"
    )
    ApiResponse<List<RoadmapResponseDTO.ListItemDTO>> getRoadmaps(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "로드맵 상세 조회",
            description = "로드맵 기본 정보, 진행률, 다음 태스크와 다음 일정을 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "로드맵 상세 조회 성공",
            content = @Content(schema = @Schema(
                    implementation = RoadmapResponseDTO.DetailResultDTO.class
            ))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "로드맵을 찾을 수 없거나 다른 회원의 로드맵"
    )
    ApiResponse<RoadmapResponseDTO.DetailResultDTO> getRoadmap(
            @Parameter(description = "로드맵 ID", example = "1", required = true)
            @Positive(message = "로드맵 ID는 양수여야 합니다.")
            @PathVariable Long roadmapId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "로드맵 일정 설정 및 변경",
            description = "출국일과 체류 기간을 변경하고 전체 태스크의 권장 완료일을 다시 계산합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "로드맵 일정 변경 성공",
            content = @Content(schema = @Schema(
                    implementation = RoadmapResponseDTO.UpdateScheduleResultDTO.class
            ))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "출국일 또는 체류 기간이 유효하지 않음"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "로드맵을 찾을 수 없거나 다른 회원의 로드맵"
    )
    ApiResponse<RoadmapResponseDTO.UpdateScheduleResultDTO> updateSchedule(
            @Parameter(description = "로드맵 ID", example = "1", required = true)
            @Positive(message = "로드맵 ID는 양수여야 합니다.")
            @PathVariable Long roadmapId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "변경할 일정 정보",
                    required = true
            )
            @Valid @RequestBody RoadmapRequestDTO.UpdateScheduleDTO request
    );

    @Operation(
            summary = "로드맵 삭제",
            description = "로그인한 회원 소유의 로드맵과 하위 데이터를 삭제합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "로드맵 삭제 성공"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "로드맵을 찾을 수 없거나 다른 회원의 로드맵"
    )
    ApiResponse<Void> deleteRoadmap(
            @Parameter(description = "로드맵 ID", example = "1", required = true)
            @Positive(message = "로드맵 ID는 양수여야 합니다.")
            @PathVariable Long roadmapId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
