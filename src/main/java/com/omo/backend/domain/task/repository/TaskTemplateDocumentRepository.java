package com.omo.backend.domain.task.repository;

import com.omo.backend.domain.task.entity.TaskTemplateDocument;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskTemplateDocumentRepository
        extends JpaRepository<TaskTemplateDocument, Long> {

    @EntityGraph(attributePaths = {"taskTemplate", "documentTemplate"})
    List<TaskTemplateDocument> findAllByTaskTemplateRoadmapTemplateIdOrderByTaskTemplateIdAscDisplayOrderAsc(
            Long roadmapTemplateId
    );
}
