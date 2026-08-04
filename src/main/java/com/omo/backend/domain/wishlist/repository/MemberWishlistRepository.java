package com.omo.backend.domain.wishlist.repository;

import com.omo.backend.domain.wishlist.entity.MemberWishlist;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberWishlistRepository extends JpaRepository<MemberWishlist, Long> {

    boolean existsByMember_IdAndCity_CityIdAndPurpose_PurposeId(
            Long memberId,
            Long cityId,
            Long purposeId
    );

    @Query("""
            select memberWishlist
            from MemberWishlist memberWishlist
            join fetch memberWishlist.city city
            join fetch city.country
            join fetch memberWishlist.purpose
            where memberWishlist.member.id = :memberId
              and city.deletedAt is null
            order by memberWishlist.createdAt desc, memberWishlist.id desc
            """)
    List<MemberWishlist> findAllActiveByMemberIdOrderByCreatedAtDescIdDesc(
            @Param("memberId") Long memberId
    );

    @Query("""
            select memberWishlist.city.cityId
            from MemberWishlist memberWishlist
            where memberWishlist.member.id = :memberId
              and memberWishlist.city.deletedAt is null
            """)
    List<Long> findWishlistedCityIdsByMemberId(@Param("memberId") Long memberId);

    @Modifying
    @Query("""
            delete from MemberWishlist memberWishlist
            where memberWishlist.member.id = :memberId
              and memberWishlist.city.cityId = :cityId
              and memberWishlist.purpose.purposeId = :purposeId
            """)
    int deleteByMemberIdAndCityIdAndPurposeId(
            @Param("memberId") Long memberId,
            @Param("cityId") Long cityId,
            @Param("purposeId") Long purposeId
    );
}
