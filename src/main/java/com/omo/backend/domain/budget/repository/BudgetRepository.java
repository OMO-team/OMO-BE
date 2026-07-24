package com.omo.backend.domain.budget.repository;

import com.omo.backend.domain.budget.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    @Modifying
    @Query("delete from Budget budget where budget.roadmap.id = :roadmapId")
    void deleteByRoadmapId(@Param("roadmapId") Long roadmapId);
}
