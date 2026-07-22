package com.omo.backend.domain.aisearch.repository;

import com.omo.backend.domain.aisearch.entity.AiSearchSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiSearchSessionRepository extends JpaRepository<AiSearchSession, Long> {
}
