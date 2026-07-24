package com.omo.backend.domain.budget.service;

import com.omo.backend.domain.budget.converter.BudgetConverter;
import com.omo.backend.domain.budget.dto.BudgetRequestDTO;
import com.omo.backend.domain.budget.dto.BudgetResponseDTO;
import com.omo.backend.domain.budget.entity.Budget;
import com.omo.backend.domain.budget.exception.BudgetErrorCode;
import com.omo.backend.domain.budget.exception.BudgetException;
import com.omo.backend.domain.budget.repository.BudgetRepository;
import com.omo.backend.domain.city.entity.City;
import com.omo.backend.domain.roadmap.entity.Roadmap;
import com.omo.backend.domain.roadmap.exception.RoadmapErrorCode;
import com.omo.backend.domain.roadmap.exception.RoadmapException;
import com.omo.backend.domain.roadmap.repository.RoadmapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BudgetCommandService {

    private final RoadmapRepository roadmapRepository;
    private final BudgetRepository budgetRepository;

    public BudgetResponseDTO.UpsertResultDTO upsertBudget(
            Long roadmapId,
            Long memberId,
            BudgetRequestDTO.UpsertDTO request
    ) {
        Roadmap roadmap = roadmapRepository.findOwnedByIdForUpdate(roadmapId, memberId)
                .orElseThrow(() -> new RoadmapException(RoadmapErrorCode.ROADMAP_NOT_FOUND));

        roadmap.updateStayMonths(request.stayMonths());
        Budget budget = budgetRepository.findByRoadmap_Id(roadmapId)
                .orElseGet(() -> createBudget(roadmap));

        return BudgetConverter.toUpsertResultDTO(budget);
    }

    private Budget createBudget(Roadmap roadmap) {
        City city = roadmap.getRoadmapTemplate().getCity();
        if (city.getInitialSettlementCost() == null || city.getMonthlyCost() == null) {
            throw new BudgetException(BudgetErrorCode.CITY_BUDGET_NOT_CONFIGURED);
        }

        Budget budget = Budget.create(
                roadmap,
                city.getInitialSettlementCost().longValue(),
                city.getMonthlyCost().longValue()
        );
        return budgetRepository.save(budget);
    }
}
