package com.omo.backend.domain.task.service;

import com.omo.backend.domain.roadmap.exception.RoadmapErrorCode;
import com.omo.backend.domain.roadmap.exception.RoadmapException;
import com.omo.backend.domain.task.entity.TaskTemplate;
import com.omo.backend.domain.task.entity.TaskTemplateDependency;
import com.omo.backend.domain.task.entity.TaskTemplateDocument;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class TaskTemplateValidator {

    public void validate(
            Long roadmapTemplateId,
            List<TaskTemplate> taskTemplates,
            List<TaskTemplateDependency> dependencies,
            List<TaskTemplateDocument> documents
    ) {
        if (taskTemplates.isEmpty()) {
            throw new RoadmapException(RoadmapErrorCode.EMPTY_ROADMAP_TEMPLATE);
        }

        Set<Long> taskTemplateIds = validateTaskTemplates(roadmapTemplateId, taskTemplates);
        validateDependencies(taskTemplateIds, dependencies);
        validateDocuments(taskTemplateIds, documents);
    }

    private Set<Long> validateTaskTemplates(
            Long roadmapTemplateId,
            List<TaskTemplate> taskTemplates
    ) {
        Set<Long> taskTemplateIds = new HashSet<>();
        for (TaskTemplate template : taskTemplates) {
            if (!roadmapTemplateId.equals(template.getRoadmapTemplate().getId())
                    || template.getId() == null
                    || !taskTemplateIds.add(template.getId())) {
                throw new RoadmapException(RoadmapErrorCode.INVALID_TEMPLATE_DEPENDENCY);
            }
        }
        return taskTemplateIds;
    }

    private void validateDependencies(
            Set<Long> taskTemplateIds,
            List<TaskTemplateDependency> dependencies
    ) {
        Map<Long, List<Long>> graph = new HashMap<>();
        taskTemplateIds.forEach(id -> graph.put(id, new ArrayList<>()));
        Set<DependencyKey> keys = new HashSet<>();

        for (TaskTemplateDependency dependency : dependencies) {
            Long taskId = dependency.getTaskTemplate().getId();
            Long prerequisiteId = dependency.getPrerequisiteTaskTemplate().getId();

            if (!taskTemplateIds.contains(taskId) || !taskTemplateIds.contains(prerequisiteId)) {
                throw new RoadmapException(RoadmapErrorCode.INVALID_TEMPLATE_DEPENDENCY);
            }
            if (taskId.equals(prerequisiteId)) {
                throw new RoadmapException(RoadmapErrorCode.SELF_REFERENCING_DEPENDENCY);
            }
            if (!keys.add(new DependencyKey(taskId, prerequisiteId))) {
                throw new RoadmapException(RoadmapErrorCode.DUPLICATE_TEMPLATE_DEPENDENCY);
            }
            graph.get(prerequisiteId).add(taskId);
        }

        Map<Long, VisitState> states = new HashMap<>();
        for (Long taskTemplateId : taskTemplateIds) {
            detectCycle(taskTemplateId, graph, states);
        }
    }

    private void detectCycle(
            Long taskTemplateId,
            Map<Long, List<Long>> graph,
            Map<Long, VisitState> states
    ) {
        VisitState state = states.get(taskTemplateId);
        if (state == VisitState.VISITING) {
            throw new RoadmapException(RoadmapErrorCode.CYCLIC_TEMPLATE_DEPENDENCY);
        }
        if (state == VisitState.VISITED) {
            return;
        }

        states.put(taskTemplateId, VisitState.VISITING);
        for (Long nextTaskId : graph.get(taskTemplateId)) {
            detectCycle(nextTaskId, graph, states);
        }
        states.put(taskTemplateId, VisitState.VISITED);
    }

    private void validateDocuments(
            Set<Long> taskTemplateIds,
            List<TaskTemplateDocument> documents
    ) {
        Set<DocumentKey> keys = new HashSet<>();
        for (TaskTemplateDocument document : documents) {
            Long taskTemplateId = document.getTaskTemplate().getId();
            if (!taskTemplateIds.contains(taskTemplateId)
                    || document.getDocumentTemplate() == null
                    || document.getDocumentTemplate().getId() == null) {
                throw new RoadmapException(RoadmapErrorCode.INVALID_TEMPLATE_DEPENDENCY);
            }

            DocumentKey key = new DocumentKey(
                    taskTemplateId,
                    document.getDocumentTemplate().getId()
            );
            if (!keys.add(key)) {
                throw new RoadmapException(RoadmapErrorCode.DUPLICATE_TEMPLATE_DOCUMENT);
            }
        }
    }

    private enum VisitState {
        VISITING,
        VISITED
    }

    private record DependencyKey(Long taskTemplateId, Long prerequisiteTaskTemplateId) {
    }

    private record DocumentKey(Long taskTemplateId, Long documentTemplateId) {
    }
}
