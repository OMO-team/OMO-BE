package com.omo.backend.domain.task.repository;

import com.omo.backend.domain.task.entity.TaskTemplate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskTemplateRepository extends JpaRepository<TaskTemplate, Long> {

    List<TaskTemplate> findAllByRoadmapTemplateIdOrderByDisplayOrderAsc(Long roadmapTemplateId);
}
