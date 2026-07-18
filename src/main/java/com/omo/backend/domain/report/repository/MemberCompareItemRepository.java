package com.omo.backend.domain.report.repository;

import com.omo.backend.domain.report.entity.MemberCompareItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberCompareItemRepository extends JpaRepository<MemberCompareItem,Long> {
    List<MemberCompareItem> findByMemberIdOrderByCreatedAtAsc(Long memberId);
    int countByMemberId(Long memberId);
    boolean existsByMemberIdAndCityId(Long memberId,  Long cityId);
    void deleteByMemberIdAndCityId(Long memberId,  Long cityId);
}
