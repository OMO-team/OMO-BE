package com.omo.backend.domain.myhome.controller;

import com.omo.backend.domain.city.dto.CityResponseDTO;
import com.omo.backend.domain.roadmap.dto.RoadmapResponseDTO;
import com.omo.backend.domain.roadmap.service.RoadmapQueryService;
import com.omo.backend.domain.wishlist.service.WishlistQueryService;
import com.omo.backend.global.apiPayload.ApiResponse;
import com.omo.backend.global.security.CustomUserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/my-home")
public class MyHomeController implements MyHomeControllerDocs {

    private final RoadmapQueryService roadmapQueryService;
    private final WishlistQueryService wishlistQueryService;

    @Override
    @GetMapping("/roadmaps")
    public ApiResponse<List<RoadmapResponseDTO.ListItemDTO>> getRoadmaps(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.onSuccess(
                roadmapQueryService.getRoadmaps(userDetails.getMemberId())
        );
    }

    @Override
    @GetMapping("/wishlist")
    public ApiResponse<CityResponseDTO.WishlistCityListResult> getWishlist(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.onSuccess(
                wishlistQueryService.getWishlist(userDetails.getMemberId())
        );
    }
}
