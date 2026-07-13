package com.omo.backend.domain.report.service;

import com.omo.backend.domain.report.converter.ReportConverter;
import com.omo.backend.domain.report.dto.ReportResponseDTO;
import com.omo.backend.domain.report.entity.CityCoreSummary;
import com.omo.backend.domain.report.entity.CityProsCons;
import com.omo.backend.domain.report.entity.CityRelatedResource;
import com.omo.backend.domain.report.exception.ReportErrorCode;
import com.omo.backend.domain.report.exception.ReportException;
import com.omo.backend.domain.report.enums.ResourceTopic;
import com.omo.backend.domain.report.repository.CityCoreSummaryRepository;
import com.omo.backend.domain.report.repository.CityProsConsRepository;
import com.omo.backend.domain.report.repository.CityRelatedResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportQueryService {

    private final CityCoreSummaryRepository cityCoreSummaryRepository;
    private final CityProsConsRepository cityProsConsRepository;
    private final CityRelatedResourceRepository cityRelatedResourceRepository;
    // private final CityRepository cityRepository;

    public List<ReportResponseDTO.CoreSummaryDTO> getCoreSummaries(Long cityId) {
        validateCityExists(cityId);
        List<CityCoreSummary> summaries = cityCoreSummaryRepository.findByCityIdAndDeletedAtIsNull(cityId);
        return ReportConverter.toCoreSummaryDTOList(summaries);
    }

    public ReportResponseDTO.ProsConsDTO getProsCons (Long cityId) {
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
        // TODO: City entity 연동 후 존재 여부 검증 로직 추가 (CITY404_1)
        // cityRepository.findById(cityId)
        //         .orElseThrow(() -> new ReportException(ReportErrorCode.CITY_NOT_FOUND));
    }
}
