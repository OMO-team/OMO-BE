package com.omo.backend.domain.roadmap.repository;

import com.omo.backend.domain.roadmap.entity.Roadmap;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {

    Optional<Roadmap> findByIdAndMember_Id(Long roadmapId, Long memberId);

    @EntityGraph(attributePaths = {
            "roadmapTemplate",
            "roadmapTemplate.city",
            "roadmapTemplate.purpose"
    })
    List<Roadmap> findAllByMember_IdOrderByCreatedAtDescIdDesc(Long memberId);

    @EntityGraph(attributePaths = {
            "roadmapTemplate",
            "roadmapTemplate.city",
            "roadmapTemplate.purpose"
    })
    Optional<Roadmap> findWithRoadmapTemplateByIdAndMember_Id(
            Long roadmapId,
            Long memberId
    );
}
