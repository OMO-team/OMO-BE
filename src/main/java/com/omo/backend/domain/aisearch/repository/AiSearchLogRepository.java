package com.omo.backend.domain.aisearch.repository;

import com.omo.backend.domain.aisearch.entity.AiSearchLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiSearchLogRepository extends JpaRepository<AiSearchLog, Long> {
    Optional<AiSearchLog> findByTaskId(String taskId);
}
