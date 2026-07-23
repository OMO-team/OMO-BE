package com.omo.backend.domain.roadmap.service;

import com.omo.backend.domain.city.entity.City;
import com.omo.backend.domain.city.exception.CityErrorCode;
import com.omo.backend.domain.city.exception.CityException;
import com.omo.backend.domain.city.repository.CityPurposeRepository;
import com.omo.backend.domain.city.repository.CityRepository;
import com.omo.backend.domain.document.entity.TaskDocument;
import com.omo.backend.domain.document.repository.TaskDocumentRepository;
import com.omo.backend.domain.member.entity.Member;
import com.omo.backend.domain.member.exception.MemberErrorCode;
import com.omo.backend.domain.member.exception.MemberException;
import com.omo.backend.domain.member.repository.MemberRepository;
import com.omo.backend.domain.purpose.entity.Purpose;
import com.omo.backend.domain.purpose.exception.PurposeErrorCode;
import com.omo.backend.domain.purpose.exception.PurposeException;
import com.omo.backend.domain.purpose.repository.PurposeRepository;
import com.omo.backend.domain.roadmap.converter.RoadmapConverter;
import com.omo.backend.domain.roadmap.dto.RoadmapRequestDTO;
import com.omo.backend.domain.roadmap.dto.RoadmapResponseDTO;
import com.omo.backend.domain.roadmap.entity.Roadmap;
import com.omo.backend.domain.roadmap.entity.RoadmapTemplate;
import com.omo.backend.domain.roadmap.exception.RoadmapErrorCode;
import com.omo.backend.domain.roadmap.exception.RoadmapException;
import com.omo.backend.domain.roadmap.repository.RoadmapRepository;
import com.omo.backend.domain.roadmap.service.RoadmapTemplateFinder.TemplateData;
import com.omo.backend.domain.task.entity.Task;
import com.omo.backend.domain.task.entity.TaskDependency;
import com.omo.backend.domain.task.entity.TaskTemplate;
import com.omo.backend.domain.task.entity.TaskTemplateDependency;
import com.omo.backend.domain.task.entity.TaskTemplateDocument;
import com.omo.backend.domain.task.repository.TaskDependencyRepository;
import com.omo.backend.domain.task.repository.TaskRepository;
import com.omo.backend.domain.task.service.TaskTemplateValidator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoadmapCreationService {

    private final MemberRepository memberRepository;
    private final CityRepository cityRepository;
    private final PurposeRepository purposeRepository;
    private final CityPurposeRepository cityPurposeRepository;
    private final RoadmapTemplateFinder roadmapTemplateFinder;
    private final TaskTemplateValidator taskTemplateValidator;
    private final RoadmapRepository roadmapRepository;
    private final TaskRepository taskRepository;
    private final TaskDependencyRepository taskDependencyRepository;
    private final TaskDocumentRepository taskDocumentRepository;

    @Transactional
    public RoadmapResponseDTO.CreateResultDTO create(
            Long memberId,
            RoadmapRequestDTO.CreateDTO request
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        City city = cityRepository.findById(request.cityId())
                .orElseThrow(() -> new CityException(CityErrorCode.CITY_NOT_FOUND));
        Purpose purpose = purposeRepository.findById(request.purposeId())
                .orElseThrow(() -> new PurposeException(PurposeErrorCode.PURPOSE_NOT_FOUND));

        validateCityPurpose(city, purpose);
        TemplateData templateData = roadmapTemplateFinder.find(city.getCityId(), purpose.getPurposeId());
        taskTemplateValidator.validate(
                templateData.template().getId(),
                templateData.taskTemplates(),
                templateData.dependencies(),
                templateData.documents()
        );

        Roadmap roadmap = createRoadmap(member, templateData.template());
        Map<Long, Task> tasksByTemplateId = createTasks(
                roadmap,
                templateData.taskTemplates()
        );
        createDependencies(templateData.dependencies(), tasksByTemplateId);
        createTaskDocuments(templateData.documents(), tasksByTemplateId);

        return RoadmapConverter.toCreateResultDTO(roadmap, tasksByTemplateId.size());
    }

    private void validateCityPurpose(City city, Purpose purpose) {
        if (!cityPurposeRepository.existsByCityCityIdAndPurposePurposeId(
                city.getCityId(),
                purpose.getPurposeId()
        )) {
            throw new RoadmapException(RoadmapErrorCode.UNSUPPORTED_CITY_PURPOSE);
        }
    }

    private Roadmap createRoadmap(
            Member member,
            RoadmapTemplate template
    ) {
        Roadmap roadmap = Roadmap.create(
                member,
                template
        );
        return roadmapRepository.save(roadmap);
    }

    private Map<Long, Task> createTasks(
            Roadmap roadmap,
            List<TaskTemplate> taskTemplates
    ) {
        List<Task> tasks = taskTemplates.stream()
                .map(template -> Task.create(roadmap, template))
                .toList();
        taskRepository.saveAll(tasks);

        Map<Long, Task> tasksByTemplateId = new HashMap<>();
        for (int index = 0; index < taskTemplates.size(); index++) {
            tasksByTemplateId.put(taskTemplates.get(index).getId(), tasks.get(index));
        }
        return tasksByTemplateId;
    }

    private void createDependencies(
            List<TaskTemplateDependency> templateDependencies,
            Map<Long, Task> tasksByTemplateId
    ) {
        Set<TaskRelationKey> keys = new HashSet<>();
        List<TaskDependency> dependencies = templateDependencies.stream()
                .map(templateDependency -> {
                    Long taskTemplateId = templateDependency.getTaskTemplate().getId();
                    Long prerequisiteTemplateId = templateDependency
                            .getPrerequisiteTaskTemplate()
                            .getId();
                    if (!keys.add(new TaskRelationKey(taskTemplateId, prerequisiteTemplateId))) {
                        throw new RoadmapException(RoadmapErrorCode.DUPLICATE_TASK_DEPENDENCY);
                    }
                    return TaskDependency.create(
                            tasksByTemplateId.get(taskTemplateId),
                            tasksByTemplateId.get(prerequisiteTemplateId)
                    );
                })
                .toList();
        taskDependencyRepository.saveAll(dependencies);
    }

    private void createTaskDocuments(
            List<TaskTemplateDocument> templateDocuments,
            Map<Long, Task> tasksByTemplateId
    ) {
        Set<TaskDocumentKey> keys = new HashSet<>();
        List<TaskDocument> taskDocuments = templateDocuments.stream()
                .map(templateDocument -> {
                    Long taskTemplateId = templateDocument.getTaskTemplate().getId();
                    Long documentTemplateId = templateDocument.getDocumentTemplate().getId();
                    if (!keys.add(new TaskDocumentKey(taskTemplateId, documentTemplateId))) {
                        throw new RoadmapException(RoadmapErrorCode.DUPLICATE_TASK_DOCUMENT);
                    }
                    return TaskDocument.createTaskDocument(
                            tasksByTemplateId.get(taskTemplateId),
                            templateDocument.getDocumentTemplate()
                    );
                })
                .toList();
        taskDocumentRepository.saveAll(taskDocuments);
    }

    private record TaskRelationKey(Long taskTemplateId, Long prerequisiteTemplateId) {
    }

    private record TaskDocumentKey(Long taskTemplateId, Long documentTemplateId) {
    }
}
