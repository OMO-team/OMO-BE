package com.omo.backend.domain.wishlist.entity;

import com.omo.backend.domain.city.entity.City;
import com.omo.backend.domain.member.entity.Member;
import com.omo.backend.domain.purpose.entity.Purpose;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "member_wishlist",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_member_wishlist_member_city",
                columnNames = {"member_id", "city_id"}
        ),
        indexes = {
                @Index(
                        name = "idx_member_wishlist_member_created_id",
                        columnList = "member_id, created_at, id"
                ),
                @Index(
                        name = "idx_member_wishlist_city",
                        columnList = "city_id"
                )
        }
)
public class MemberWishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purpose_id", nullable = false)
    private Purpose purpose;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder(access = AccessLevel.PRIVATE)
    private MemberWishlist(Member member, City city, Purpose purpose) {
        this.member = member;
        this.city = city;
        this.purpose = purpose;
    }

    public static MemberWishlist create(Member member, City city, Purpose purpose) {
        return MemberWishlist.builder()
                .member(member)
                .city(city)
                .purpose(purpose)
                .build();
    }
}
