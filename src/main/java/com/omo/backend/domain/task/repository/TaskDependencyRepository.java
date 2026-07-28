package com.omo.backend.domain.task.repository;

import com.omo.backend.domain.task.entity.TaskDependency;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskDependencyRepository extends JpaRepository<TaskDependency, Long> {

    @EntityGraph(attributePaths = "prerequisiteTask")
    List<TaskDependency> findAllByTask_Id(Long taskId);

    @EntityGraph(attributePaths = {"task", "prerequisiteTask"})
    List<TaskDependency> findAllByTask_Roadmap_IdIn(List<Long> roadmapIds);

    @Modifying
    @Query("""
            delete from TaskDependency dependency
            where dependency.task.id in (
                select task.id from Task task where task.roadmap.id = :roadmapId
            )
            """)
    void deleteAllByRoadmapId(@Param("roadmapId") Long roadmapId);
}
