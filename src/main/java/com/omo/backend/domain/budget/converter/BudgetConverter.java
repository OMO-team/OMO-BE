package com.omo.backend.domain.budget.converter;

import com.omo.backend.domain.budget.dto.BudgetResponseDTO;
import com.omo.backend.domain.budget.entity.Budget;

public final class BudgetConverter {

    private BudgetConverter() {
    }

    public static BudgetResponseDTO.UpsertResultDTO toUpsertResultDTO(Budget budget) {
        return BudgetResponseDTO.UpsertResultDTO.builder()
                .roadmapId(budget.getRoadmap().getId())
                .stayMonths(budget.getRoadmap().getStayMonths())
                .initialSettlementCost(budget.getInitialSettlementCost())
                .monthlyCost(budget.getMonthlyCost())
                .totalCost(budget.calculateTotalCost())
                .build();
    }
}
