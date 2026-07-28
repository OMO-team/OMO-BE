package com.omo.backend.domain.budget.repository;

import com.omo.backend.domain.budget.entity.Budget;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByRoadmap_Id(Long roadmapId);

    @Modifying
    @Query("delete from Budget budget where budget.roadmap.id = :roadmapId")
    void deleteByRoadmapId(@Param("roadmapId") Long roadmapId);
}
