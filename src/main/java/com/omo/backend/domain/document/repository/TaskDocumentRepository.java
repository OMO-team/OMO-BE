package com.omo.backend.domain.document.repository;

import com.omo.backend.domain.document.entity.TaskDocument;
import jakarta.persistence.LockModeType;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskDocumentRepository
        extends JpaRepository<TaskDocument, Long> {

    @EntityGraph(attributePaths = "documentTemplate")
    List<TaskDocument> findAllByTask_IdOrderByIdAsc(Long taskId);

    @EntityGraph(attributePaths = "task")
    List<TaskDocument> findAllByTask_Roadmap_IdIn(List<Long> roadmapIds);

    @Modifying
    @Query("""
            delete from TaskDocument taskDocument
            where taskDocument.task.id in (
                select task.id from Task task where task.roadmap.id = :roadmapId
            )
            """)
    void deleteAllByRoadmapId(@Param("roadmapId") Long roadmapId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select taskDocument
            from TaskDocument taskDocument
            where taskDocument.task.id = :taskId
              and taskDocument.task.roadmap.member.id = :memberId
            order by taskDocument.id asc
            """)
    List<TaskDocument> findAllForUpdateByTaskIdAndMemberId(
            @Param("taskId") Long taskId,
            @Param("memberId") Long memberId
    );
}
