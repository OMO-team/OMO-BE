package com.omo.backend.domain.report.service;

import com.omo.backend.domain.city.repository.CityRepository;
import com.omo.backend.domain.report.entity.MemberCompareItem;
import com.omo.backend.domain.report.exception.ReportErrorCode;
import com.omo.backend.domain.report.exception.ReportException;
import com.omo.backend.domain.report.repository.MemberCompareItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportCommandService {
    private final MemberCompareItemRepository memberCompareItemRepository;
    private final CityRepository cityRepository;

    public void addCompareItem(Long memberId, Long cityId) {
        if (memberCompareItemRepository.countByMemberId(memberId) >= 3) {
            throw new ReportException(ReportErrorCode.COMPARE_ITEM_LIMIT_EXCEEDED);
        }
        if (memberCompareItemRepository.existsByMemberIdAndCityId(memberId, cityId)) {
            throw new ReportException(ReportErrorCode.COMPARE_ITEM_ALREADY_EXISTS);
        }
        if(!cityRepository.existsById(cityId)) {
            throw new ReportException(ReportErrorCode.CITY_NOT_FOUND);
        }
        memberCompareItemRepository.save(MemberCompareItem.create(memberId, cityId));
    }

    public void removeCompareItem(Long memberId, Long cityId) {
        if (!memberCompareItemRepository.existsByMemberIdAndCityId(memberId, cityId)) {
            throw new ReportException(ReportErrorCode.COMPARE_ITEM_NOT_FOUND);
        }
        memberCompareItemRepository.deleteByMemberIdAndCityId(memberId, cityId);
    }
}
