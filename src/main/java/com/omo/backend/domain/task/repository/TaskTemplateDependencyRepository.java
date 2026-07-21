package com.omo.backend.domain.task.repository;

import com.omo.backend.domain.task.entity.TaskTemplateDependency;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskTemplateDependencyRepository
        extends JpaRepository<TaskTemplateDependency, Long> {

    @EntityGraph(attributePaths = {"taskTemplate", "prerequisiteTaskTemplate"})
    List<TaskTemplateDependency> findAllByTaskTemplateRoadmapTemplateId(Long roadmapTemplateId);
}
