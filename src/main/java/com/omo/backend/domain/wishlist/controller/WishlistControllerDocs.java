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
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Wishlist", description = "위시리스트 API")
public interface WishlistControllerDocs {

    @Operation(
            summary = "위시리스트 도시 추가",
            description = "POST 요청으로 로그인한 회원의 위시리스트에 도시와 선택 목적을 추가합니다. 동일한 도시·목적 조합이 이미 있으면 성공 처리합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "위시리스트 추가 성공"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "도시·목적 ID 검증 실패, 타입 오류 또는 지원하지 않는 도시·목적 조합",
            content = @Content(schema = @Schema(
                    oneOf = {
                            ApiResponse.ErrorResponse.class,
                            ApiResponse.ValidationErrorResponse.class
                    }
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
            description = "회원을 찾을 수 없거나 도시 또는 목적이 존재하지 않음",
            content = @Content(schema = @Schema(
                    implementation = ApiResponse.ErrorResponse.class
            ))
    )
    ApiResponse<Void> addWishlist(
            @Parameter(description = "추가할 도시 ID", example = "1", required = true)
            @Positive(message = "도시 ID는 양수여야 합니다.")
            @PathVariable Long cityId,

            @Parameter(description = "찜한 도시의 목적 ID", example = "3", required = true)
            @Positive(message = "목적 ID는 양수여야 합니다.")
            @RequestParam Long purposeId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "위시리스트 도시 삭제",
            description = "로그인한 회원의 위시리스트에서 도시·목적 조합을 삭제합니다. 존재하지 않는 항목도 성공 처리합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "위시리스트 삭제 성공"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "도시·목적 ID 검증 실패 또는 타입 오류",
            content = @Content(schema = @Schema(
                    oneOf = {
                            ApiResponse.ErrorResponse.class,
                            ApiResponse.ValidationErrorResponse.class
                    }
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

            @Parameter(description = "삭제할 위시리스트의 목적 ID", example = "3", required = true)
            @Positive(message = "목적 ID는 양수여야 합니다.")
            @RequestParam Long purposeId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
