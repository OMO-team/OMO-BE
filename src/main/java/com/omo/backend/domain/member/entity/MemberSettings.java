package com.omo.backend.domain.member.entity;

import com.omo.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Builder(access = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@DynamicUpdate
@DynamicInsert
@Table(name = "member_settings")
public class MemberSettings extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "push_notification", nullable = false)
    private Boolean pushNotification;

    @Column(name = "email_notification", nullable = false)
    private Boolean emailNotification;

    @Column(name = "auto_save", nullable = false)
    private Boolean autoSave;

    @Column(name = "two_factor_enabled", nullable = false)
    private Boolean twoFactorEnabled;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    public static MemberSettings createDefaultSettings(Member member) {
        return MemberSettings.builder()
                .member(member)
                .pushNotification(true)
                .emailNotification(true)
                .autoSave(true)
                .twoFactorEnabled(false)
                .build();
    }

    public void updateSettings(
            Boolean pushNotification,
            Boolean emailNotification,
            Boolean autoSave,
            Boolean twoFactorEnabled
    ) {
        if (pushNotification != null) {
            this.pushNotification = pushNotification;
        }
        if (emailNotification != null) {
            this.emailNotification = emailNotification;
        }
        if (autoSave != null) {
            this.autoSave = autoSave;
        }
        if (twoFactorEnabled != null) {
            this.twoFactorEnabled = twoFactorEnabled;
        }
    }
}
