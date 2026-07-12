package com.omo.backend.domain.document.entity;

import com.omo.backend.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "task_document")
public class TaskDocument extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // TODO: Roadmap 도메인의 Task 엔티티 구현 후 @ManyToOne 연관관계로 변경
    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_template_id", nullable = false)
    private DocumentTemplate documentTemplate;

    @Column(name = "is_checked", nullable = false)
    private Boolean checked;

    @Column(name = "checked_at", nullable = true)
    private LocalDateTime checkedAt;

    public static TaskDocument createTaskDocument(
            Long taskId,
            DocumentTemplate documentTemplate
    ) {
        return TaskDocument.builder()
                .taskId(taskId)
                .documentTemplate(documentTemplate)
                .checked(false)
                .build();
    }

    public void updateChecked(Boolean checked) {
        this.checked = checked;
        this.checkedAt = checked ? LocalDateTime.now() : null;
    }
}
