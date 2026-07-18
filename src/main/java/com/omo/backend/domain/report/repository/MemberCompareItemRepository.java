package com.omo.backend.domain.report.repository;

import com.omo.backend.domain.report.entity.MemberCompareItem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberCompareItemRepository extends JpaRepository<MemberCompareItem,Long> {
    List<MemberCompareItem> findByMemberIdOrderByCreatedAtAsc(Long memberId);
    int countByMemberId(Long memberId);
    boolean existsByMemberIdAndCityId(Long memberId,  Long cityId);
    void deleteByMemberIdAndCityId(Long memberId,  Long cityId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from MemberCompareItem c where c.memberId = :memberId")
    List<MemberCompareItem> findByMemberIdForUpdate(@Param("memberId") Long memberId);
}
