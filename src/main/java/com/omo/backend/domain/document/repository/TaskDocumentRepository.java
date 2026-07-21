package com.omo.backend.domain.document.repository;

import com.omo.backend.domain.document.entity.TaskDocument;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskDocumentRepository
        extends JpaRepository<TaskDocument, Long> {

    @EntityGraph(attributePaths = "documentTemplate")
    List<TaskDocument> findAllByTask_IdOrderByIdAsc(Long taskId);

    @EntityGraph(attributePaths = "task")
    List<TaskDocument> findAllByTask_Roadmap_Id(Long roadmapId);
}
