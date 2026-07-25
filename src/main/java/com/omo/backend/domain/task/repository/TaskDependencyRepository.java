package com.omo.backend.domain.task.repository;

import com.omo.backend.domain.task.entity.TaskDependency;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskDependencyRepository extends JpaRepository<TaskDependency, Long> {

    @EntityGraph(attributePaths = {"task", "prerequisiteTask"})
    List<TaskDependency> findAllByTask_Roadmap_Id(Long roadmapId);

    @EntityGraph(attributePaths = "prerequisiteTask")
    List<TaskDependency> findAllByTask_Id(Long taskId);
}
