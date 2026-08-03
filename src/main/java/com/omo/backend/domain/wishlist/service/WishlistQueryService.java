package com.omo.backend.domain.wishlist.service;

import com.omo.backend.domain.city.converter.CityConverter;
import com.omo.backend.domain.city.dto.CityResponseDTO;
import com.omo.backend.domain.wishlist.repository.MemberWishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistQueryService {

    private final MemberWishlistRepository memberWishlistRepository;

    public CityResponseDTO.WishlistCityListResult getWishlist(Long memberId) {
        return CityConverter.toWishlistCityListResult(
                memberWishlistRepository.findAllActiveByMemberIdOrderByCreatedAtDescIdDesc(memberId)
        );
    }
}
