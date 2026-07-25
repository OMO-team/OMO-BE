package com.omo.backend.domain.wishlist.controller;

import com.omo.backend.domain.wishlist.service.WishlistCommandService;
import com.omo.backend.global.apiPayload.ApiResponse;
import com.omo.backend.global.security.CustomUserDetails;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wishlist")
@Validated
public class WishlistController implements WishlistControllerDocs {

    private final WishlistCommandService wishlistCommandService;

    @Override
    @PostMapping("/{cityId}")
    public ApiResponse<Void> addWishlist(
            @Positive(message = "도시 ID는 양수여야 합니다.")
            @PathVariable Long cityId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        wishlistCommandService.addWishlist(userDetails.getMemberId(), cityId);
        return ApiResponse.onSuccess(null);
    }

    @Override
    @DeleteMapping("/{cityId}")
    public ApiResponse<Void> removeWishlist(
            @Positive(message = "도시 ID는 양수여야 합니다.")
            @PathVariable Long cityId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        wishlistCommandService.removeWishlist(userDetails.getMemberId(), cityId);
        return ApiResponse.onSuccess(null);
    }
}
