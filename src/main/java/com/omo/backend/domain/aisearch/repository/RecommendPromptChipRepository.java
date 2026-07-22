package com.omo.backend.domain.aisearch.repository;

import com.omo.backend.domain.aisearch.entity.RecommendPromptChip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendPromptChipRepository extends JpaRepository<RecommendPromptChip, Long> {
    List<RecommendPromptChip> findAllByIsActiveTrue();
}
