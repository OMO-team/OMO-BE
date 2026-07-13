package com.omo.backend.domain.member.entity;

import com.omo.backend.common.BaseEntity;
import com.omo.backend.domain.member.enums.MemberProvider;
import com.omo.backend.domain.member.enums.MemberStatus;
import com.omo.backend.domain.roadmap.entity.Roadmap;
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
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", length = 20, nullable = false)
    private MemberProvider provider;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private MemberStatus status;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private MemberSettings memberSettings;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MemberTerms> memberTermsList = new ArrayList<>();

    public static Member createLocalMember(String email, String password, String name) {
        return Member.builder()
                .email(email)
                .password(password)
                .name(name)
                .provider(MemberProvider.LOCAL)
                .emailVerified(false)
                .status(MemberStatus.ACTIVE)
                .build();
    }

    // Roadmap 양방향 매핑
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Roadmap> roadmaps = new ArrayList<>();

}
