package com.omo.backend.domain.report.converter;

import com.omo.backend.domain.city.entity.City;
import com.omo.backend.domain.report.dto.ReportResponseDTO;
import com.omo.backend.domain.report.entity.CityCoreSummary;
import com.omo.backend.domain.report.entity.CityProsCons;
import com.omo.backend.domain.report.entity.CityRelatedResource;
import com.omo.backend.domain.report.entity.CityReview;
import com.omo.backend.domain.report.entity.MemberCompareItem;
import com.omo.backend.domain.report.enums.ProsConsType;
import com.omo.backend.domain.report.enums.StatType;

import java.util.List;

public class ReportConverter {

    public static ReportResponseDTO.CoreSummaryDTO toCoreSummaryDTO(CityCoreSummary coreSummary) {
        return new ReportResponseDTO.CoreSummaryDTO(
                coreSummary.getCategory(),
                coreSummary.getTitle(),
                coreSummary.getContent()
        );
    }

    public static List<ReportResponseDTO.CoreSummaryDTO> toCoreSummaryDTOList(List<CityCoreSummary> coreSummary) {
        return coreSummary.stream()
                .map(ReportConverter::toCoreSummaryDTO)
                .toList();
    }

    public static ReportResponseDTO.ProsConsDTO toProsConsDTO (List<CityProsCons> prosCons) {
        List<String> pros = prosCons.stream()
                .filter(e -> e.getType() == ProsConsType.PROS)
                .map(CityProsCons::getContent)
                .toList();

        List<String> cons = prosCons.stream()
                .filter(e -> e.getType() == ProsConsType.CONS)
                .map(CityProsCons::getContent)
                .toList();

        return new ReportResponseDTO.ProsConsDTO(pros, cons, pros.isEmpty(), cons.isEmpty());
    }

    public static ReportResponseDTO.ResourceDTO toResourceDTO(CityRelatedResource resource) {
        return new ReportResponseDTO.ResourceDTO(
                resource.getTopic().name(),
                resource.getResourceType().name(),
                resource.getTitle(),
                resource.getSource(),
                resource.getUrl()
        );
    }

    public static List<ReportResponseDTO.ResourceDTO> toResourceDTOList(List<CityRelatedResource> resource) {
        return resource.stream()
                .map(ReportConverter::toResourceDTO)
                .toList();
    }

    public static ReportResponseDTO.CityReviewDTO toCityReviewDTO(CityReview review) {
        return new ReportResponseDTO.CityReviewDTO(
                review.getAuthorName(),
                review.getRating(),
                review.getContent()
        );
    }

    public static List<ReportResponseDTO.CityReviewDTO> toCityReviewDTOList(List<CityReview> reviews) {
        return reviews.stream()
                .map(ReportConverter::toCityReviewDTO)
                .toList();
    }

    public static List<ReportResponseDTO.StatDTO> toStatDTOList(City city) {
        return List.of(
                new ReportResponseDTO.StatDTO(StatType.SAFETY, toDouble(city.getSafetyScore()), 5.0, "점"),
                new ReportResponseDTO.StatDTO(StatType.COST, toDouble(city.getMonthlyCost()), null, "원"),
                new ReportResponseDTO.StatDTO(StatType.HOUSING, toDouble(city.getHousingScore()), 5.0, "점"),
                new ReportResponseDTO.StatDTO(StatType.VISA, toDouble(city.getVisaScore()), 5.0, "점"),
                new ReportResponseDTO.StatDTO(StatType.INFRA, toDouble(city.getInfraScore()), 5.0, "점"),
                new ReportResponseDTO.StatDTO(StatType.INTERNET, toDouble(city.getInternetScore()), 5.0, "점"),
                new ReportResponseDTO.StatDTO(StatType.PREFERENCE, toDouble(city.getPreferenceScore()), 5.0, "점")
        );
    }

    private static Double toDouble(Number number) {
        return number == null ? null : number.doubleValue();
    }

    public static ReportResponseDTO.CityHeaderDTO toCityHeaderDTO(City city) {
        return new ReportResponseDTO.CityHeaderDTO(
                city.getCityId(),
                city.getName(),
                city.getCountry().getName(),
                city.getImageUrl(),
                toDouble(city.getRating())
        );
    }

    public static ReportResponseDTO.CompareResultDTO toCompareResultDTO(List<City> cities) {
        List<ReportResponseDTO.CityHeaderDTO> cityHeaders = cities.stream()
                .map(ReportConverter::toCityHeaderDTO)
                .toList();

        List<ReportResponseDTO.StatGroupDTO> statGroups = List.of(
                toStatGroupDTO(StatType.SAFETY, 5.0, "점", cities, City::getSafetyScore),
                toStatGroupDTO(StatType.COST, null, "원", cities, City::getMonthlyCost),
                toStatGroupDTO(StatType.HOUSING, 5.0, "점", cities, City::getHousingScore),
                toStatGroupDTO(StatType.VISA, 5.0, "점", cities, City::getVisaScore),
                toStatGroupDTO(StatType.INFRA, 5.0, "점", cities, City::getInfraScore),
                toStatGroupDTO(StatType.INTERNET, 5.0, "점", cities, City::getInternetScore),
                toStatGroupDTO(StatType.PREFERENCE, 5.0, "점", cities, City::getPreferenceScore)

        );

        return new ReportResponseDTO.CompareResultDTO(cityHeaders, statGroups);
    }

    private static ReportResponseDTO.StatGroupDTO toStatGroupDTO(
            StatType statType, Double maxValue, String unit,
            List<City> cities, java.util.function.Function<City, Number> valueExtractor
    ) {
        List<ReportResponseDTO.CityValueDTO> cityValues = cities.stream()
                .map(city -> new ReportResponseDTO.CityValueDTO(city.getCityId(), toDouble(valueExtractor.apply(city))))
                .toList();
        return new ReportResponseDTO.StatGroupDTO(statType, maxValue, unit, cityValues);
    }

    public static ReportResponseDTO.CompareItemDTO toCompareItemDTO(MemberCompareItem item) {
        return new ReportResponseDTO.CompareItemDTO(item.getCityId(), item.getCreatedAt());
    }

    public static List<ReportResponseDTO.CompareItemDTO> toCompareItemDTOList(List<MemberCompareItem> items) {
        return items.stream()
                .map(ReportConverter::toCompareItemDTO)
                .toList();
    }
}
