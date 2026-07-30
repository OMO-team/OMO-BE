package com.omo.backend.domain.wishlist.controller;

import com.omo.backend.global.apiPayload.ApiResponse;
import com.omo.backend.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Wishlist", description = "위시리스트 API")
public interface WishlistControllerDocs {

    @Operation(
            summary = "위시리스트 도시 추가",
            description = "POST 요청으로 로그인한 회원의 위시리스트에 삭제되지 않은 도시를 추가합니다. 이미 추가된 도시는 성공 처리합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "위시리스트 추가 성공"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "도시 ID 검증 실패 또는 타입 오류",
            content = @Content(schema = @Schema(
                    implementation = ApiResponse.ErrorResponse.class
            ))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 필요",
            content = @Content(schema = @Schema(
                    implementation = ApiResponse.ErrorResponse.class
            ))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "도시를 찾을 수 없거나 삭제된 도시",
            content = @Content(schema = @Schema(
                    implementation = ApiResponse.ErrorResponse.class
            ))
    )
    ApiResponse<Void> addWishlist(
            @Parameter(description = "추가할 도시 ID", example = "1", required = true)
            @Positive(message = "도시 ID는 양수여야 합니다.")
            @PathVariable Long cityId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "위시리스트 도시 삭제",
            description = "로그인한 회원의 위시리스트에서 도시를 삭제합니다. 존재하지 않는 항목도 성공 처리합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "위시리스트 삭제 성공"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "도시 ID 검증 실패 또는 타입 오류",
            content = @Content(schema = @Schema(
                    implementation = ApiResponse.ErrorResponse.class
            ))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 필요",
            content = @Content(schema = @Schema(
                    implementation = ApiResponse.ErrorResponse.class
            ))
    )
    ApiResponse<Void> removeWishlist(
            @Parameter(description = "삭제할 도시 ID", example = "1", required = true)
            @Positive(message = "도시 ID는 양수여야 합니다.")
            @PathVariable Long cityId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
