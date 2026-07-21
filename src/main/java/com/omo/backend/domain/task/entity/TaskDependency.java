package com.omo.backend.domain.task.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "task_dependency",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_task_dependency",
                columnNames = {"task_id", "prerequisite_task_id"}
        )
)
public class TaskDependency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prerequisite_task_id", nullable = false)
    private Task prerequisiteTask;

    public static TaskDependency create(Task task, Task prerequisiteTask) {
        if (task == prerequisiteTask) {
            throw new IllegalArgumentException("태스크는 자기 자신을 선행 태스크로 가질 수 없습니다.");
        }
        if (task.getRoadmap() != prerequisiteTask.getRoadmap()) {
            throw new IllegalArgumentException("서로 다른 로드맵의 태스크를 연결할 수 없습니다.");
        }

        return TaskDependency.builder()
                .task(task)
                .prerequisiteTask(prerequisiteTask)
                .build();
    }
}
