package com.omo.backend.domain.roadmap.service;

import com.omo.backend.domain.budget.repository.BudgetRepository;
import com.omo.backend.domain.document.repository.TaskDocumentRepository;
import com.omo.backend.domain.roadmap.converter.RoadmapConverter;
import com.omo.backend.domain.roadmap.dto.RoadmapRequestDTO;
import com.omo.backend.domain.roadmap.dto.RoadmapResponseDTO;
import com.omo.backend.domain.roadmap.entity.Roadmap;
import com.omo.backend.domain.roadmap.exception.RoadmapErrorCode;
import com.omo.backend.domain.roadmap.exception.RoadmapException;
import com.omo.backend.domain.roadmap.repository.RoadmapRepository;
import com.omo.backend.domain.task.entity.Task;
import com.omo.backend.domain.task.repository.TaskDependencyRepository;
import com.omo.backend.domain.task.repository.TaskRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoadmapCommandService {

    private final RoadmapRepository roadmapRepository;
    private final TaskRepository taskRepository;
    private final TaskDependencyRepository taskDependencyRepository;
    private final TaskDocumentRepository taskDocumentRepository;
    private final BudgetRepository budgetRepository;
    private final RoadmapScheduleCalculator roadmapScheduleCalculator;

    public RoadmapResponseDTO.UpdateScheduleResultDTO updateSchedule(
            Long roadmapId,
            Long memberId,
            RoadmapRequestDTO.UpdateScheduleDTO request
    ) {
        LocalDate today = LocalDate.now();
        validateDepartureDate(request.departureDate(), today);
        Roadmap roadmap = getOwnedRoadmap(roadmapId, memberId);
        boolean isInitialDepartureDate = roadmap.getDepartureDate() == null;
        List<Task> tasks = taskRepository
                .findAllWithTaskTemplateByRoadmap_IdOrderByDisplayOrderAscIdAsc(roadmapId);

        roadmap.updateDepartureDate(request.departureDate());
        if (isInitialDepartureDate) {
            tasks.forEach(task -> task.initializeDueDate(request.departureDate()));
        }

        return RoadmapConverter.toUpdateScheduleResultDTO(
                roadmap,
                roadmapScheduleCalculator.calculateDDay(
                        request.departureDate(),
                        today
                ),
                tasks
        );
    }

    public void deleteRoadmap(Long roadmapId, Long memberId) {
        Roadmap roadmap = getOwnedRoadmap(roadmapId, memberId);

        taskDependencyRepository.deleteAllByRoadmapId(roadmapId);
        taskDocumentRepository.deleteAllByRoadmapId(roadmapId);
        budgetRepository.deleteByRoadmapId(roadmapId);
        taskRepository.deleteAllByRoadmapId(roadmapId);
        roadmapRepository.delete(roadmap);
    }

    private Roadmap getOwnedRoadmap(Long roadmapId, Long memberId) {
        return roadmapRepository.findByIdAndMember_Id(roadmapId, memberId)
                .orElseThrow(() -> new RoadmapException(RoadmapErrorCode.ROADMAP_NOT_FOUND));
    }

    private void validateDepartureDate(LocalDate departureDate, LocalDate today) {
        if (departureDate.isBefore(today)) {
            throw new RoadmapException(RoadmapErrorCode.INVALID_DEPARTURE_DATE);
        }
    }
}
