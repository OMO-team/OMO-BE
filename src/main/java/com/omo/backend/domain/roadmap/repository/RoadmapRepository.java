package com.omo.backend.domain.roadmap.repository;

import com.omo.backend.domain.roadmap.entity.Roadmap;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {

    @EntityGraph(attributePaths = {
            "roadmapTemplate",
            "roadmapTemplate.city",
            "roadmapTemplate.city.country",
            "roadmapTemplate.purpose"
    })
    List<Roadmap> findAllByMember_IdOrderByCreatedAtDescIdDesc(Long memberId);

    @EntityGraph(attributePaths = {
            "roadmapTemplate",
            "roadmapTemplate.city",
            "roadmapTemplate.purpose",
            "budget"
    })
    Optional<Roadmap> findWithRoadmapTemplateByIdAndMember_Id(
            Long roadmapId,
            Long memberId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select roadmap
            from Roadmap roadmap
            where roadmap.id = :roadmapId
              and roadmap.member.id = :memberId
            """)
    Optional<Roadmap> findOwnedByIdForUpdate(
            @Param("roadmapId") Long roadmapId,
            @Param("memberId") Long memberId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select roadmap
            from Task task
            join task.roadmap roadmap
            where task.id = :taskId
              and roadmap.member.id = :memberId
            """)
    Optional<Roadmap> findOwnedByTaskIdForUpdate(
            @Param("taskId") Long taskId,
            @Param("memberId") Long memberId
    );
}
