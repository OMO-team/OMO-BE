package com.omo.backend.domain.wishlist.service;

import com.omo.backend.domain.city.converter.CityConverter;
import com.omo.backend.domain.city.dto.CityResponseDTO;
import com.omo.backend.domain.city.entity.City;
import com.omo.backend.domain.wishlist.repository.MemberWishlistRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistQueryService {

    private final MemberWishlistRepository memberWishlistRepository;

    public CityResponseDTO.WishlistCityListResult getWishlist(Long memberId) {
        List<City> cities = memberWishlistRepository
                .findAllActiveByMemberIdOrderByCreatedAtDescIdDesc(memberId)
                .stream()
                .map(memberWishlist -> memberWishlist.getCity())
                .toList();

        return CityConverter.toWishlistCityListResult(cities);
    }
}
