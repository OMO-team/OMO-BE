package com.omo.backend.domain.member.service;

import com.omo.backend.domain.member.converter.MemberConverter;
import com.omo.backend.domain.member.entity.Member;
import com.omo.backend.domain.member.entity.MemberTerms;
import com.omo.backend.domain.member.exception.MemberErrorCode;
import com.omo.backend.domain.member.exception.MemberException;
import com.omo.backend.domain.member.repository.MemberTermsRepository;
import com.omo.backend.domain.terms.entity.Terms;
import com.omo.backend.domain.terms.repository.TermsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TermsAgreementService {

    private final TermsRepository termsRepository;
    private final MemberTermsRepository memberTermsRepository;

    // 전달받은 약관 ID가 모두 존재하고 필수 약관을 포함하는지 검증
    public List<Terms> validateAndGetAgreedTerms(List<Long> agreedTermsIds) {
        List<Terms> agreedTerms = termsRepository.findAllById(agreedTermsIds);
        validateAgreedTerms(agreedTermsIds, agreedTerms);
        validateRequiredTermsAgreed(agreedTerms);
        return agreedTerms;
    }

    // 검증된 약관을 회원 동의 이력으로 저장
    public void saveMemberTerms(Member member, List<Terms> agreedTerms) {
        List<MemberTerms> memberTermsList = agreedTerms.stream()
                .map(terms -> MemberConverter.toMemberTerms(member, terms))
                .toList();
        memberTermsRepository.saveAll(memberTermsList);
    }

    private void validateAgreedTerms(List<Long> agreedTermsIds, List<Terms> agreedTerms) {
        Set<Long> uniqueAgreedTermsIds = new HashSet<>(agreedTermsIds);
        if (uniqueAgreedTermsIds.size() != agreedTerms.size()) {
            throw new MemberException(MemberErrorCode.INVALID_AGREED_TERMS);
        }
    }

    private void validateRequiredTermsAgreed(List<Terms> agreedTerms) {
        Set<Long> agreedTermsIds = new HashSet<>(
                agreedTerms.stream()
                        .map(Terms::getId)
                        .toList()
        );

        // 필수 약관 중 하나라도 빠져 있으면 회원가입을 막음
        boolean hasMissingRequiredTerms = termsRepository.findAllByRequiredTrueAndDeletedAtIsNull()
                .stream()
                .map(Terms::getId)
                .anyMatch(requiredTermsId -> !agreedTermsIds.contains(requiredTermsId));

        if (hasMissingRequiredTerms) {
            throw new MemberException(MemberErrorCode.REQUIRED_TERMS_NOT_AGREED);
        }
    }
}
