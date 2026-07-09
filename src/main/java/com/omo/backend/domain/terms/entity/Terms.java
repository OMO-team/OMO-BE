package com.omo.backend.domain.terms.entity;

import com.omo.backend.common.BaseEntity;
import com.omo.backend.domain.member.entity.MemberTerms;
import com.omo.backend.domain.terms.enums.TermsType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder(access = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@DynamicUpdate
@DynamicInsert
@Table(name = "terms")
public class Terms extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30, nullable = false)
    private TermsType type;

    @Column(name = "required", nullable = false)
    private Boolean required;

    @Column(name = "version", length = 20, nullable = false)
    private String version;

    @Column(name = "effective_at", nullable = false)
    private LocalDateTime effectiveAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "terms", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MemberTerms> memberTermsList = new ArrayList<>();

    public static Terms createTerms(
            String title,
            String content,
            TermsType type,
            Boolean required,
            String version,
            LocalDateTime effectiveAt
    ) {
        return Terms.builder()
                .title(title)
                .content(content)
                .type(type)
                .required(required)
                .version(version)
                .effectiveAt(effectiveAt)
                .build();
    }
}
