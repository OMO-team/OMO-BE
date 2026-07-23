package com.omo.backend.domain.roadmap.service;

import com.omo.backend.domain.roadmap.entity.RoadmapTemplate;
import com.omo.backend.domain.roadmap.exception.RoadmapErrorCode;
import com.omo.backend.domain.roadmap.exception.RoadmapException;
import com.omo.backend.domain.roadmap.repository.RoadmapTemplateRepository;
import com.omo.backend.domain.task.entity.TaskTemplate;
import com.omo.backend.domain.task.entity.TaskTemplateDependency;
import com.omo.backend.domain.task.entity.TaskTemplateDocument;
import com.omo.backend.domain.task.repository.TaskTemplateDependencyRepository;
import com.omo.backend.domain.task.repository.TaskTemplateDocumentRepository;
import com.omo.backend.domain.task.repository.TaskTemplateRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoadmapTemplateFinder {

    private final RoadmapTemplateRepository roadmapTemplateRepository;
    private final TaskTemplateRepository taskTemplateRepository;
    private final TaskTemplateDependencyRepository taskTemplateDependencyRepository;
    private final TaskTemplateDocumentRepository taskTemplateDocumentRepository;

    public TemplateData find(Long cityId, Long purposeId) {
        List<RoadmapTemplate> templates = roadmapTemplateRepository
                .findAllByCityCityIdAndPurposePurposeIdOrderByIdAsc(cityId, purposeId);
        if (templates.isEmpty()) {
            throw new RoadmapException(RoadmapErrorCode.ROADMAP_TEMPLATE_NOT_FOUND);
        }
        if (templates.size() > 1) {
            throw new RoadmapException(RoadmapErrorCode.AMBIGUOUS_ROADMAP_TEMPLATE);
        }
        RoadmapTemplate template = templates.getFirst();

        List<TaskTemplate> taskTemplates = taskTemplateRepository
                .findAllByRoadmapTemplateIdOrderByDisplayOrderAsc(template.getId());
        List<TaskTemplateDependency> dependencies = taskTemplateDependencyRepository
                .findAllByTaskTemplateRoadmapTemplateId(template.getId());
        List<TaskTemplateDocument> documents = taskTemplateDocumentRepository
                .findAllByTaskTemplateRoadmapTemplateIdOrderByTaskTemplateIdAscDisplayOrderAsc(
                        template.getId()
                );

        return new TemplateData(template, taskTemplates, dependencies, documents);
    }

    public record TemplateData(
            RoadmapTemplate template,
            List<TaskTemplate> taskTemplates,
            List<TaskTemplateDependency> dependencies,
            List<TaskTemplateDocument> documents
    ) {
    }
}
