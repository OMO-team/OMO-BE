package com.omo.backend.domain.roadmap.repository;

import com.omo.backend.domain.roadmap.entity.RoadmapTemplate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadmapTemplateRepository extends JpaRepository<RoadmapTemplate, Long> {

    List<RoadmapTemplate> findAllByCityCityIdAndPurposePurposeIdOrderByIdAsc(
            Long cityId,
            Long purposeId
    );
}
