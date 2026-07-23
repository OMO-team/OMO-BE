package com.omo.backend.domain.roadmap.service;

import com.omo.backend.domain.document.entity.TaskDocument;
import com.omo.backend.domain.document.repository.TaskDocumentRepository;
import com.omo.backend.domain.roadmap.converter.RoadmapConverter;
import com.omo.backend.domain.roadmap.dto.RoadmapResponseDTO;
import com.omo.backend.domain.roadmap.entity.Roadmap;
import com.omo.backend.domain.roadmap.exception.RoadmapErrorCode;
import com.omo.backend.domain.roadmap.exception.RoadmapException;
import com.omo.backend.domain.roadmap.repository.RoadmapRepository;
import com.omo.backend.domain.task.entity.Task;
import com.omo.backend.domain.task.entity.TaskDependency;
import com.omo.backend.domain.task.enums.TaskStatus;
import com.omo.backend.domain.task.repository.TaskDependencyRepository;
import com.omo.backend.domain.task.repository.TaskRepository;
import com.omo.backend.domain.task.service.TaskStatusCalculator;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoadmapQueryService {

    private final RoadmapRepository roadmapRepository;
    private final TaskRepository taskRepository;
    private final TaskDependencyRepository taskDependencyRepository;
    private final TaskDocumentRepository taskDocumentRepository;
    private final TaskStatusCalculator taskStatusCalculator;
    private final RoadmapProgressCalculator roadmapProgressCalculator;
    private final RoadmapScheduleCalculator roadmapScheduleCalculator;

    public List<RoadmapResponseDTO.ListItemDTO> getRoadmaps(Long memberId) {
        List<Roadmap> roadmaps = roadmapRepository
                .findAllByMember_IdOrderByCreatedAtDescIdDesc(memberId);
        if (roadmaps.isEmpty()) {
            return Collections.emptyList();
        }

        OverviewData overviewData = loadOverviewData(
                roadmaps.stream().map(Roadmap::getId).toList()
        );
        LocalDate today = LocalDate.now();

        return roadmaps.stream()
                .map(roadmap -> {
                    RoadmapOverview overview = calculateOverview(
                            roadmap,
                            overviewData,
                            today
                    );
                    return RoadmapConverter.toListItemDTO(
                            roadmap,
                            overview.completedTaskCount(),
                            overview.tasks().size(),
                            overview.progressRate(),
                            overview.nextTask(),
                            overview.nextScheduleTask(),
                            overview.departureDDay(),
                            overview.nextScheduleDDay(),
                            overview.isNextScheduleOverdue()
                    );
                })
                .toList();
    }

    public RoadmapResponseDTO.DetailResultDTO getRoadmap(
            Long roadmapId,
            Long memberId
    ) {
        Roadmap roadmap = roadmapRepository
                .findWithRoadmapTemplateByIdAndMember_Id(roadmapId, memberId)
                .orElseThrow(() -> new RoadmapException(RoadmapErrorCode.ROADMAP_NOT_FOUND));
        OverviewData overviewData = loadOverviewData(List.of(roadmapId));
        RoadmapOverview overview = calculateOverview(
                roadmap,
                overviewData,
                LocalDate.now()
        );

        return RoadmapConverter.toDetailResultDTO(
                roadmap,
                overview.completedTaskCount(),
                overview.tasks().size(),
                overview.progressRate(),
                overview.nextTask(),
                overview.nextScheduleTask(),
                overview.departureDDay(),
                overview.nextScheduleDDay(),
                overview.isNextScheduleOverdue()
        );
    }

    private OverviewData loadOverviewData(List<Long> roadmapIds) {
        List<Task> tasks = taskRepository
                .findAllByRoadmap_IdInOrderByDisplayOrderAscIdAsc(roadmapIds);
        Map<Long, List<Task>> tasksByRoadmapId = tasks.stream()
                .collect(Collectors.groupingBy(task -> task.getRoadmap().getId()));
        Map<Long, List<TaskDependency>> dependenciesByTaskId = taskDependencyRepository
                .findAllByTask_Roadmap_IdIn(roadmapIds)
                .stream()
                .collect(Collectors.groupingBy(dependency -> dependency.getTask().getId()));
        Map<Long, List<TaskDocument>> documentsByTaskId = taskDocumentRepository
                .findAllByTask_Roadmap_IdIn(roadmapIds)
                .stream()
                .collect(Collectors.groupingBy(TaskDocument::getTaskId));

        return new OverviewData(tasksByRoadmapId, dependenciesByTaskId, documentsByTaskId);
    }

    private RoadmapOverview calculateOverview(
            Roadmap roadmap,
            OverviewData data,
            LocalDate today
    ) {
        List<Task> tasks = data.tasksByRoadmapId()
                .getOrDefault(roadmap.getId(), Collections.emptyList());
        Map<Long, TaskStatus> statusByTaskId = tasks.stream()
                .collect(Collectors.toMap(
                        Task::getId,
                        task -> taskStatusCalculator.calculate(
                                task,
                                data.dependenciesByTaskId().getOrDefault(
                                        task.getId(),
                                        Collections.emptyList()
                                ),
                                data.documentsByTaskId().getOrDefault(
                                        task.getId(),
                                        Collections.emptyList()
                                )
                        )
                ));

        Task nextTask = tasks.stream()
                .filter(task -> !task.isCompleted())
                .filter(task -> statusByTaskId.get(task.getId()) != TaskStatus.LOCKED)
                .findFirst()
                .orElse(null);
        Task nextScheduleTask = tasks.stream()
                .filter(task -> !task.isCompleted())
                .filter(task -> task.getDueDate() != null)
                .min(Comparator.comparing(Task::getDueDate)
                        .thenComparing(Task::getDisplayOrder)
                        .thenComparing(Task::getId))
                .orElse(null);
        List<TaskDocument> documents = tasks.stream()
                .map(Task::getId)
                .map(taskId -> data.documentsByTaskId()
                        .getOrDefault(taskId, Collections.emptyList()))
                .flatMap(List::stream)
                .toList();
        long completedTaskCount = tasks.stream().filter(Task::isCompleted).count();
        Long nextScheduleDDay = nextScheduleTask == null
                ? null
                : roadmapScheduleCalculator.calculateDDay(nextScheduleTask.getDueDate(), today);
        Boolean isNextScheduleOverdue = nextScheduleTask == null
                ? null
                : roadmapScheduleCalculator.isOverdue(
                        nextScheduleTask.getDueDate(),
                        today,
                        nextScheduleTask.isCompleted()
                );

        return new RoadmapOverview(
                tasks,
                completedTaskCount,
                roadmapProgressCalculator.calculate(documents),
                nextTask,
                nextScheduleTask,
                roadmapScheduleCalculator.calculateDDay(roadmap.getDepartureDate(), today),
                nextScheduleDDay,
                isNextScheduleOverdue
        );
    }

    private record OverviewData(
            Map<Long, List<Task>> tasksByRoadmapId,
            Map<Long, List<TaskDependency>> dependenciesByTaskId,
            Map<Long, List<TaskDocument>> documentsByTaskId
    ) {
    }

    private record RoadmapOverview(
            List<Task> tasks,
            long completedTaskCount,
            double progressRate,
            Task nextTask,
            Task nextScheduleTask,
            Long departureDDay,
            Long nextScheduleDDay,
            Boolean isNextScheduleOverdue
    ) {
    }
}
