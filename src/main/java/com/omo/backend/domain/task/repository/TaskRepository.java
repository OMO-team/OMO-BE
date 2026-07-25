package com.omo.backend.domain.task.repository;

import com.omo.backend.domain.task.entity.Task;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByRoadmap_IdOrderByDisplayOrderAscIdAsc(Long roadmapId);

    Optional<Task> findByIdAndRoadmap_Member_Id(Long taskId, Long memberId);
}
