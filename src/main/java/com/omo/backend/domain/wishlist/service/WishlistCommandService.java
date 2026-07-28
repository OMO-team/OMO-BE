package com.omo.backend.domain.wishlist.service;

import com.omo.backend.domain.city.entity.City;
import com.omo.backend.domain.city.exception.CityErrorCode;
import com.omo.backend.domain.city.exception.CityException;
import com.omo.backend.domain.city.repository.CityRepository;
import com.omo.backend.domain.member.entity.Member;
import com.omo.backend.domain.member.exception.MemberErrorCode;
import com.omo.backend.domain.member.exception.MemberException;
import com.omo.backend.domain.member.repository.MemberRepository;
import com.omo.backend.domain.wishlist.entity.MemberWishlist;
import com.omo.backend.domain.wishlist.repository.MemberWishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistCommandService {

    private final MemberWishlistRepository memberWishlistRepository;
    private final MemberRepository memberRepository;
    private final CityRepository cityRepository;

    public void addWishlist(Long memberId, Long cityId) {
        Member member = memberRepository.findByIdForUpdate(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        City city = cityRepository.findByCityIdAndDeletedAtIsNull(cityId)
                .orElseThrow(() -> new CityException(CityErrorCode.CITY_NOT_FOUND));

        if (memberWishlistRepository.existsByMember_IdAndCity_CityId(memberId, cityId)) {
            return;
        }

        memberWishlistRepository.save(MemberWishlist.create(member, city));
    }

    public void removeWishlist(Long memberId, Long cityId) {
        memberWishlistRepository.deleteByMemberIdAndCityId(memberId, cityId);
    }
}
