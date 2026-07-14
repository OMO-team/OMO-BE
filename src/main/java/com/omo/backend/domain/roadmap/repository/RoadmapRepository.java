package com.omo.backend.domain.roadmap.repository;

import com.omo.backend.domain.roadmap.entity.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {
}
