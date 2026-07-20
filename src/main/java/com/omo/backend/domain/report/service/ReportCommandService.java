package com.omo.backend.domain.report.service;

import com.omo.backend.domain.city.repository.CityRepository;
import com.omo.backend.domain.member.repository.MemberRepository;
import com.omo.backend.domain.report.entity.MemberCompareItem;
import com.omo.backend.domain.report.exception.ReportErrorCode;
import com.omo.backend.domain.report.exception.ReportException;
import com.omo.backend.domain.report.repository.MemberCompareItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportCommandService {
    private final MemberCompareItemRepository memberCompareItemRepository;
    private final CityRepository cityRepository;
    private final MemberRepository memberRepository;

    public void addCompareItem(Long memberId, Long cityId) {
        memberRepository.findByIdForUpdate(memberId)
                .orElseThrow(() -> new IllegalStateException("인증된 회원 정보를 찾을 수 없습니다: " + memberId));

        List<MemberCompareItem> existingItems = memberCompareItemRepository.findByMemberIdForUpdate(memberId);

        if (existingItems.size() >= 3) {
            throw new ReportException(ReportErrorCode.COMPARE_ITEM_LIMIT_EXCEEDED);
        }
        boolean alreadyExists = existingItems.stream()
                .anyMatch(item -> item.getCityId().equals(cityId));
        if (alreadyExists) {
            throw new ReportException(ReportErrorCode.COMPARE_ITEM_ALREADY_EXISTS);
        }
        if (!cityRepository.existsById(cityId)) {
            throw new ReportException(ReportErrorCode.CITY_NOT_FOUND);
        }
        try {
            memberCompareItemRepository.save(MemberCompareItem.create(memberId, cityId));
        } catch (DataIntegrityViolationException e) {
            throw new ReportException(ReportErrorCode.COMPARE_ITEM_ALREADY_EXISTS);
        }
    }

    public void removeCompareItem(Long memberId, Long cityId) {
        if (!memberCompareItemRepository.existsByMemberIdAndCityId(memberId, cityId)) {
            throw new ReportException(ReportErrorCode.COMPARE_ITEM_NOT_FOUND);
        }
        memberCompareItemRepository.deleteByMemberIdAndCityId(memberId, cityId);
    }
}
