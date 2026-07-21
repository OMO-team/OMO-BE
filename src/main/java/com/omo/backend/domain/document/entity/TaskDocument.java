package com.omo.backend.domain.document.entity;

import com.omo.backend.common.BaseEntity;
import com.omo.backend.domain.task.entity.Task;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Builder(access = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@DynamicUpdate
@DynamicInsert
@Table(
        name = "task_document",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_task_document_task_template",
                        columnNames = {"task_id", "document_template_id"}
                )
        }
)
public class TaskDocument extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_template_id", nullable = false)
    private DocumentTemplate documentTemplate;

    @Column(name = "is_checked", nullable = false)
    private Boolean checked;

    @Column(name = "checked_at")
    private LocalDateTime checkedAt;

    public static TaskDocument createTaskDocument(
            Task task,
            DocumentTemplate documentTemplate
    ) {
        return TaskDocument.builder()
                .task(task)
                .documentTemplate(documentTemplate)
                .checked(false)
                .build();
    }

    public Long getTaskId() {
        return task.getId();
    }

    public void updateChecked(Boolean checked) {
        this.checked = checked;
        this.checkedAt = checked ? LocalDateTime.now() : null;
    }
}
