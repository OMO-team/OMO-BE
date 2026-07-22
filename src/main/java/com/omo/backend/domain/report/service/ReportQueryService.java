package com.omo.backend.domain.report.service;

import com.omo.backend.domain.city.entity.City;
import com.omo.backend.domain.city.repository.CityRepository;
import com.omo.backend.domain.report.converter.ReportConverter;
import com.omo.backend.domain.report.dto.ReportResponseDTO;
import com.omo.backend.domain.report.entity.CityCoreSummary;
import com.omo.backend.domain.report.entity.CityProsCons;
import com.omo.backend.domain.report.entity.CityRelatedResource;
import com.omo.backend.domain.report.entity.MemberCompareItem;
import com.omo.backend.domain.report.exception.ReportErrorCode;
import com.omo.backend.domain.report.exception.ReportException;
import com.omo.backend.domain.report.enums.ResourceTopic;
import com.omo.backend.domain.report.repository.CityCoreSummaryRepository;
import com.omo.backend.domain.report.repository.CityProsConsRepository;
import com.omo.backend.domain.report.repository.CityRelatedResourceRepository;
import com.omo.backend.domain.report.repository.MemberCompareItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportQueryService {

    private final CityCoreSummaryRepository cityCoreSummaryRepository;
    private final CityProsConsRepository cityProsConsRepository;
    private final CityRelatedResourceRepository cityRelatedResourceRepository;
    private final CityRepository cityRepository;
    private final MemberCompareItemRepository memberCompareItemRepository;

    public List<ReportResponseDTO.CoreSummaryDTO> getCoreSummaries(Long cityId) {
        validateCityExists(cityId);
        List<CityCoreSummary> summaries = cityCoreSummaryRepository.findByCityIdAndDeletedAtIsNull(cityId);
        return ReportConverter.toCoreSummaryDTOList(summaries);
    }

    public ReportResponseDTO.ProsConsDTO getProsCons(Long cityId) {
        validateCityExists(cityId);
        List<CityProsCons> prosCons =
                cityProsConsRepository.findByCityIdAndDeletedAtIsNullOrderByDisplayOrderAsc(cityId);
        return ReportConverter.toProsConsDTO(prosCons);
    }

    public List<ReportResponseDTO.ResourceDTO> getResources(Long cityId, String topic) {
        validateCityExists(cityId);

        if (topic == null || topic.isBlank()) {
            List<CityRelatedResource> resources = cityRelatedResourceRepository.findByCityIdAndDeletedAtIsNull(cityId);
            return ReportConverter.toResourceDTOList(resources);
        }

        ResourceTopic resourceTopic;
        try {
            resourceTopic = ResourceTopic.valueOf(topic.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ReportException(ReportErrorCode.RESOURCE_TOPIC_INVALID);
        }

        List<CityRelatedResource> resources =
                cityRelatedResourceRepository.findByCityIdAndTopicAndDeletedAtIsNull(cityId, resourceTopic);
        return ReportConverter.toResourceDTOList(resources);
    }

    public ReportResponseDTO.AiReportDTO getAiReport(Long cityId, String question) {
        if (question == null || question.isBlank()) {
            throw new ReportException(ReportErrorCode.AI_REPORT_QUERY_EMPTY);
        }
        validateCityExists(cityId);

        String summary = generateAiSummary(cityId, question);

        List<CityRelatedResource> resources =
                cityRelatedResourceRepository.findByCityIdAndDeletedAtIsNull(cityId);

        return new ReportResponseDTO.AiReportDTO(summary, ReportConverter.toResourceDTOList(resources));
    }

    private String generateAiSummary(Long cityId, String question) {
        // TODO: 팀원 LLM 연동 코드 참고해서 실제 구현 예정
        return "AI 답변 준비 중입니다. (임시 응답)";
    }

    private void validateCityExists(Long cityId) {
        if (!cityRepository.existsById(cityId)) {
            throw new ReportException(ReportErrorCode.CITY_NOT_FOUND);
        }
    }

    public List<ReportResponseDTO.StatDTO> getStats(Long cityId) {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ReportException(ReportErrorCode.CITY_NOT_FOUND));
        return ReportConverter.toStatDTOList(city);
    }

    public ReportResponseDTO.CompareResultDTO getCompareStats(List<Long> cityIds) {
        if (cityIds.size() < 2 || cityIds.size() > 3) {
            throw new ReportException(ReportErrorCode.COMPARE_CITY_IDS_INVALID);
        }
        if (cityIds.size() != Set.copyOf(cityIds).size()) {
            throw new ReportException(ReportErrorCode.COMPARE_CITY_IDS_DUPLICATED);
        }

        List<City> cities = cityRepository.findAllWithCountryByCityIdIn(cityIds);
        if (cities.size() != cityIds.size()) {
            throw new ReportException(ReportErrorCode.CITY_NOT_FOUND);
        }

        return ReportConverter.toCompareResultDTO(cities);
    }

    public List<ReportResponseDTO.CompareItemDTO> getMyCompareItems(Long memberId) {
        List<MemberCompareItem> items = memberCompareItemRepository.findByMemberIdOrderByCreatedAtAsc(memberId);
        return ReportConverter.toCompareItemDTOList(items);
    }
}
