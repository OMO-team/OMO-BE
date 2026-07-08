package com.omo.backend.domain.member.service;

import com.omo.backend.domain.member.converter.MemberConverter;
import com.omo.backend.domain.member.dto.MemberRequestDTO;
import com.omo.backend.domain.member.dto.MemberResponseDTO;
import com.omo.backend.domain.member.entity.Member;
import com.omo.backend.domain.member.entity.MemberTerms;
import com.omo.backend.domain.member.exception.MemberErrorCode;
import com.omo.backend.domain.member.exception.MemberException;
import com.omo.backend.domain.member.repository.MemberRepository;
import com.omo.backend.domain.member.repository.MemberSettingsRepository;
import com.omo.backend.domain.terms.entity.Terms;
import com.omo.backend.domain.terms.repository.MemberTermsRepository;
import com.omo.backend.domain.terms.repository.TermsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandService {

    private final MemberRepository memberRepository;
    private final MemberSettingsRepository memberSettingsRepository;
    private final MemberTermsRepository memberTermsRepository;
    private final TermsRepository termsRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberResponseDTO.JoinResultDTO join(MemberRequestDTO.JoinDTO request) {
        // 이미 등록된 이메일인지 확인
        validateDuplicateEmail(request.email());

        // 실제 존재하는 약관인지 확인
        List<Terms> agreedTerms = termsRepository.findAllById(request.agreedTermsIds());
        validateAgreedTerms(request.agreedTermsIds(), agreedTerms);
        validateRequiredTermsAgreed(agreedTerms);

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.password());

        // 회원 기본 정보와 기본 설정을 저장
        Member member = memberRepository.save(MemberConverter.toMember(request, encodedPassword));
        memberSettingsRepository.save(MemberConverter.toDefaultMemberSettings(member));

        // 회원이 동의한 약관들을 회원-약관 매핑 테이블에 저장
        List<MemberTerms> memberTermsList = agreedTerms.stream()
                .map(terms -> MemberConverter.toMemberTerms(member, terms))
                .toList();
        memberTermsRepository.saveAll(memberTermsList);

        return MemberConverter.toJoinResultDTO(member);
    }

    private void validateDuplicateEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new MemberException(MemberErrorCode.DUPLICATE_EMAIL);
        }
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
