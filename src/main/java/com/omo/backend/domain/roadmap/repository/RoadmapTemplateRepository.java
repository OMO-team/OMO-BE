package com.omo.backend.domain.roadmap.repository;

import com.omo.backend.domain.roadmap.entity.RoadmapTemplate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadmapTemplateRepository extends JpaRepository<RoadmapTemplate, Long> {

    Optional<RoadmapTemplate> findByCityCityIdAndPurposePurposeId(Long cityId, Long purposeId);
}
