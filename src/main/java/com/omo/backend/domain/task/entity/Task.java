package com.omo.backend.domain.task.entity;

import com.omo.backend.common.BaseEntity;
import com.omo.backend.domain.roadmap.entity.Roadmap;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
@Table(name = "task")
public class Task extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id", nullable = false)
    private Roadmap roadmap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_template_id", nullable = false)
    private TaskTemplate taskTemplate;

    @Column(name = "task_name", length = 100, nullable = false)
    private String taskName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public static Task create(Roadmap roadmap, TaskTemplate template, LocalDate departureDate) {
        return Task.builder()
                .roadmap(roadmap)
                .taskTemplate(template)
                .taskName(template.getTaskName())
                .description(template.getDescription())
                .displayOrder(template.getDisplayOrder())
                .dueDate(departureDate.minusDays(template.getDaysBeforeDeparture()))
                .isCompleted(false)
                .build();
    }

    public void complete() {
        if (!Boolean.TRUE.equals(isCompleted)) {
            isCompleted = true;
            completedAt = LocalDateTime.now();
        }
    }

    public void reopen() {
        isCompleted = false;
        completedAt = null;
    }
}
