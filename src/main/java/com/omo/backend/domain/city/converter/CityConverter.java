package com.omo.backend.domain.city.converter;

import com.omo.backend.domain.city.dto.CityResponseDTO;
import com.omo.backend.domain.city.entity.City;
import com.omo.backend.domain.purpose.entity.Purpose;
import com.omo.backend.domain.wishlist.entity.MemberWishlist;

import java.util.List;
import java.util.Set;

public class CityConverter {

    // City 카드 하나
    public static CityResponseDTO.CityInfo toCityInfo(City city, boolean isWishlisted) {
        return CityResponseDTO.CityInfo.builder()
                .cityId(city.getCityId())
                .name(city.getName())
                .country(CityResponseDTO.CountryDTO.builder()
                        .countryId(city.getCountry().getCountryId())
                        .name(city.getCountry().getName())
                        .build())
                .continent(city.getCountry().getContinent())
                .imageUrl(city.getImageUrl())
                .rating(city.getRating())
                .description(city.getDescription())
                .monthlyCost(city.getMonthlyCost())
                .safetyScore(city.getSafetyScore())
                .housingScore(city.getHousingScore())
                .visaScore(city.getVisaScore())
                .languageScore(city.getLanguageScore())
                .internetScore(city.getInternetScore())
                .stayDuration(city.getStayDuration() != null ? city.getStayDuration().name() : null)
                .isWishlisted(isWishlisted)
                .build();
    }

    // 위시리스트 목록 등 isWishlisted 불필요한 경우 (모두 false)
    public static CityResponseDTO.CityListResult toCityListResult(List<City> cities) {
        return toCityListResult(cities, Set.of());
    }

    // 필터 통합 검색 (로그인 사용자 위시리스트 여부 포함)
    public static CityResponseDTO.CityListResult toCityListResult(List<City> cities, Set<Long> wishlistedCityIds) {
        List<CityResponseDTO.CityInfo> cityInfoList = cities.stream()
                .map(city -> toCityInfo(city, wishlistedCityIds.contains(city.getCityId())))
                .toList();

        return CityResponseDTO.CityListResult.builder()
                .totalCount(cityInfoList.size())
                .cities(cityInfoList)
                .build();
    }

    public static CityResponseDTO.WishlistCityListResult toWishlistCityListResult(
            List<MemberWishlist> wishlistItems
    ) {
        List<CityResponseDTO.WishlistCityInfo> cityInfoList = wishlistItems.stream()
                .map(CityConverter::toWishlistCityInfo)
                .toList();

        return CityResponseDTO.WishlistCityListResult.builder()
                .totalCount(cityInfoList.size())
                .cities(cityInfoList)
                .build();
    }

    private static CityResponseDTO.WishlistCityInfo toWishlistCityInfo(MemberWishlist wishlistItem) {
        CityResponseDTO.CityInfo cityInfo = toCityInfo(wishlistItem.getCity(), true);
        Purpose purpose = wishlistItem.getPurpose();

        return CityResponseDTO.WishlistCityInfo.builder()
                .cityId(cityInfo.cityId())
                .name(cityInfo.name())
                .country(cityInfo.country())
                .purposeId(purpose.getPurposeId())
                .purposeName(purpose.getName())
                .continent(cityInfo.continent())
                .imageUrl(cityInfo.imageUrl())
                .rating(cityInfo.rating())
                .description(cityInfo.description())
                .monthlyCost(cityInfo.monthlyCost())
                .safetyScore(cityInfo.safetyScore())
                .housingScore(cityInfo.housingScore())
                .visaScore(cityInfo.visaScore())
                .languageScore(cityInfo.languageScore())
                .internetScore(cityInfo.internetScore())
                .stayDuration(cityInfo.stayDuration())
                .isWishlisted(cityInfo.isWishlisted())
                .build();
    }
}
