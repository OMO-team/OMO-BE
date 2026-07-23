package com.omo.backend.domain.task.entity;

import com.omo.backend.common.BaseEntity;
import com.omo.backend.domain.document.entity.TaskDocument;
import com.omo.backend.domain.roadmap.entity.Roadmap;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@DynamicUpdate
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

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Builder.Default
    @OneToMany(mappedBy = "task")
    private List<TaskDependency> dependencies = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "prerequisiteTask")
    private List<TaskDependency> prerequisiteFor = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "task")
    private List<TaskDocument> taskDocuments = new ArrayList<>();

    public static Task create(Roadmap roadmap, TaskTemplate template) {
        return Task.builder()
                .roadmap(roadmap)
                .taskTemplate(template)
                .name(template.getName())
                .description(template.getDescription())
                .displayOrder(template.getDisplayOrder())
                .dueDate(calculateDueDate(
                        roadmap.getDepartureDate(),
                        template.getDaysBeforeDeparture()
                ))
                .isCompleted(false)
                .build();
    }

    public void updateDueDate(LocalDate departureDate) {
        this.dueDate = calculateDueDate(
                departureDate,
                taskTemplate.getDaysBeforeDeparture()
        );
    }

    public void complete() {
        if (!isCompleted() || completedAt == null) {
            isCompleted = true;
            completedAt = LocalDateTime.now();
        }
    }

    public void uncomplete() {
        isCompleted = false;
        completedAt = null;
    }

    public boolean isCompleted() {
        return Boolean.TRUE.equals(isCompleted);
    }

    private static LocalDate calculateDueDate(
            LocalDate departureDate,
            Integer daysBeforeDeparture
    ) {
        if (departureDate == null) {
            return null;
        }
        return departureDate.minusDays(daysBeforeDeparture);
    }
}
