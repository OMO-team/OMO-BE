package com.omo.backend.domain.report.converter;

import com.omo.backend.domain.report.dto.ReportResponseDTO;
import com.omo.backend.domain.report.entity.CityCoreSummary;
import com.omo.backend.domain.report.entity.CityProsCons;
import com.omo.backend.domain.report.entity.CityRelatedResource;
import com.omo.backend.domain.report.entity.CityReview;
import com.omo.backend.domain.report.enums.ProsConsType;

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

    //4번 stat변환은 추후 city테이블 연동후 작성 예정
}
