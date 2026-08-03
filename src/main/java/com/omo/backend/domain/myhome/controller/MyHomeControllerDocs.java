package com.omo.backend.domain.myhome.controller;

import com.omo.backend.domain.city.dto.CityResponseDTO;
import com.omo.backend.domain.roadmap.dto.RoadmapResponseDTO;
import com.omo.backend.global.apiPayload.ApiResponse;
import com.omo.backend.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "My Home", description = "내 홈 API")
public interface MyHomeControllerDocs {

    @Operation(
            summary = "내 로드맵 목록 조회",
            description = "로그인한 회원의 로드맵을 생성일과 ID의 내림차순으로 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "로드맵 목록 조회 성공"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 필요",
            content = @Content(schema = @Schema(
                    implementation = ApiResponse.ErrorResponse.class
            ))
    )
    ApiResponse<List<RoadmapResponseDTO.ListItemDTO>> getRoadmaps(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "내 위시리스트 조회",
            description = "로그인한 회원이 위시리스트에 추가한 도시를 최근 추가 순으로 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "위시리스트 조회 성공"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 필요",
            content = @Content(schema = @Schema(
                    implementation = ApiResponse.ErrorResponse.class
            ))
    )
    ApiResponse<CityResponseDTO.WishlistCityListResult> getWishlist(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
