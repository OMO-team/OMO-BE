package com.omo.backend.domain.task.entity;

import com.omo.backend.common.BaseEntity;
import com.omo.backend.domain.roadmap.entity.Roadmap;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "task")
public class Task extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id", nullable = false)
    private Roadmap roadmap;

    @Column(name = "task_name", length = 100)
    private String taskName;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "is_completed")
    private Boolean isCompleted;

    @Column(name = "priority")
    private Integer priority;

    // === 생성자 ===
    @Builder
    public Task(Roadmap roadmap, String taskName, LocalDate dueDate,
                Boolean isCompleted, Integer priority) {
        this.roadmap = roadmap;
        this.taskName = taskName;
        this.dueDate = dueDate;
        this.isCompleted = isCompleted == null ? false : isCompleted;
        this.priority = priority;
    }

    // === 연관관계 편의 메서드 ===
    public void assignRoadmap(Roadmap roadmap) {
        // 기존 객체와의 연관관계 제거 (중복 방지 안전장치)
        if (this.roadmap != null) {
            this.roadmap.getTasks().remove(this);
        }
        this.roadmap = roadmap;
        if (roadmap != null && !roadmap.getTasks().contains(this)) {
            roadmap.getTasks().add(this);
        }
    }

    // === 비즈니스 메서드 (수정자) ===
    public void complete() {
        this.isCompleted = true;
    }

    public void updateDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void updatePriority(Integer priority) {
        this.priority = priority;
    }
}