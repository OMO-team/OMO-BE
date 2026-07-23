package com.omo.backend.domain.task.repository;

import com.omo.backend.domain.task.entity.Task;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByRoadmap_IdOrderByDisplayOrderAscIdAsc(Long roadmapId);

    Optional<Task> findByIdAndRoadmap_Member_Id(Long taskId, Long memberId);

    @EntityGraph(attributePaths = {"roadmap", "taskTemplate"})
    List<Task> findAllByRoadmap_IdInOrderByDisplayOrderAscIdAsc(List<Long> roadmapIds);

    @EntityGraph(attributePaths = "taskTemplate")
    List<Task> findAllWithTaskTemplateByRoadmap_IdOrderByDisplayOrderAscIdAsc(
            Long roadmapId
    );

    @EntityGraph(attributePaths = {"roadmap", "taskTemplate"})
    Optional<Task> findWithRoadmapAndTaskTemplateByIdAndRoadmap_Member_Id(
            Long taskId,
            Long memberId
    );

    @Modifying
    @Query("delete from Task task where task.roadmap.id = :roadmapId")
    void deleteAllByRoadmapId(@Param("roadmapId") Long roadmapId);
}
