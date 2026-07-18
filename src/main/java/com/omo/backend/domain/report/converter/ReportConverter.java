package com.omo.backend.domain.report.converter;

import com.omo.backend.domain.city.entity.City;
import com.omo.backend.domain.report.dto.ReportResponseDTO;
import com.omo.backend.domain.report.entity.CityCoreSummary;
import com.omo.backend.domain.report.entity.CityProsCons;
import com.omo.backend.domain.report.entity.CityRelatedResource;
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

    public static List<ReportResponseDTO.StatDTO> toStatDTOList(City city) {
        return List.of(
                new ReportResponseDTO.StatDTO(StatType.SAFETY.name(), toDouble(city.getSafetyScore()), 5.0, "점"),
                new ReportResponseDTO.StatDTO(StatType.COST.name(), toDouble(city.getMonthlyCost()), null, "원"),
                new ReportResponseDTO.StatDTO(StatType.HOUSING.name(), toDouble(city.getHousingScore()), 5.0, "점"),
                new ReportResponseDTO.StatDTO(StatType.VISA.name(), toDouble(city.getVisaScore()), 5.0, "점"),
                new ReportResponseDTO.StatDTO(StatType.INFRA.name(), toDouble(city.getInfraScore()), 5.0, "점")
        );
    }

    private static Double toDouble(Number number) {
        return number == null ? null : number.doubleValue();
    }
}
