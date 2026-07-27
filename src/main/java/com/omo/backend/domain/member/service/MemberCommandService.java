package com.omo.backend.domain.member.service;

import com.omo.backend.domain.auth.service.EmailVerificationService;
import com.omo.backend.domain.member.converter.MemberConverter;
import com.omo.backend.domain.member.dto.MemberRequestDTO;
import com.omo.backend.domain.member.dto.MemberResponseDTO;
import com.omo.backend.domain.member.entity.Member;
import com.omo.backend.domain.member.entity.MemberSettings;
import com.omo.backend.domain.member.entity.MemberTerms;
import com.omo.backend.domain.member.enums.MemberStatus;
import com.omo.backend.domain.member.exception.MemberErrorCode;
import com.omo.backend.domain.member.exception.MemberException;
import com.omo.backend.domain.member.repository.MemberRepository;
import com.omo.backend.domain.member.repository.MemberSettingsRepository;
import com.omo.backend.domain.terms.entity.Terms;
import com.omo.backend.domain.member.repository.MemberTermsRepository;
import com.omo.backend.domain.terms.repository.TermsRepository;
import com.omo.backend.global.storage.FileValidationPolicy;
import com.omo.backend.global.storage.S3Properties;
import com.omo.backend.global.storage.exception.StorageErrorCode;
import com.omo.backend.global.storage.exception.StorageException;
import com.omo.backend.global.storage.service.S3FileService;
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
    private final EmailVerificationService emailVerificationService;
    private final S3FileService s3FileService;
    private final FileValidationPolicy fileValidationPolicy;
    private final S3Properties s3Properties;

    public MemberResponseDTO.JoinResultDTO join(MemberRequestDTO.JoinDTO request) {
        // 이미 등록된 이메일인지 확인
        validateDuplicateEmail(request.email());

        // 이메일 인증이 완료된 이메일인지 확인
        emailVerificationService.validateVerifiedEmail(request.email());

        // 비밀번호와 비밀번호 확인이 일치하는지 확인
        validatePasswordConfirm(request.password(), request.passwordConfirm());

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
        emailVerificationService.deleteVerifiedEmail(request.email());

        return MemberConverter.toJoinResultDTO(member);
    }

    public MemberResponseDTO.UpdateProfileResultDTO updateProfile(Long memberId, MemberRequestDTO.UpdateProfileDTO request) {
        Member member = getActiveMember(memberId);
        member.updateProfile(request.name());

        return MemberConverter.toUpdateProfileResultDTO(member);
    }

    public MemberResponseDTO.ProfileImageUpdateResultDTO updateProfileImage(Long memberId, MemberRequestDTO.ProfileImageUpdateDTO request) {
        // 활성 회원인지 확인하고, 다른 회원의 프로필 경로를 등록하지 못하도록 object key를 검증
        Member member = getActiveMember(memberId);
        validateProfileImageKey(memberId, request.objectKey());

        // S3에 업로드된 객체의 실제 메타데이터를 조회해 크기와 MIME 타입을 다시 검증
        S3FileService.ObjectMetadata metadata = s3FileService.getObjectMetadata(s3Properties.profileBucket(), request.objectKey());
        fileValidationPolicy.validateStoredObject(request.objectKey(), metadata.contentType(), metadata.contentLength());

        // DB에는 만료되는 Presigned URL이 아닌 영구 식별자인 object key를 저장
        String previousObjectKey = member.getProfileImageKey();
        member.updateProfileImage(request.objectKey());

        // 이미지 교체인 경우 더 이상 참조하지 않는 기존 S3 객체를 삭제
        if (previousObjectKey != null && !previousObjectKey.equals(request.objectKey())) {
            s3FileService.delete(s3Properties.profileBucket(), previousObjectKey);
        }

        return MemberConverter.toProfileImageUpdateResultDTO(member);
    }

    public void deleteProfileImage(Long memberId) {
        Member member = getActiveMember(memberId);
        String objectKey = member.getProfileImageKey();

        // 이미 프로필 이미지가 없는 경우에도 삭제 요청을 성공 처리
        if (objectKey == null) {
            return;
        }

        // DB에서 프로필 이미지 연결을 제거하고 기존 S3 객체를 삭제
        member.deleteProfileImage();
        s3FileService.delete(s3Properties.profileBucket(), objectKey);
    }

    public void withdrawMember(Long memberId) {
        Member member = getActiveMember(memberId);
        member.withdraw();
    }

    public MemberResponseDTO.SettingsResultDTO updateSettings(Long memberId, MemberRequestDTO.UpdateSettingsDTO request) {
        Member member = getActiveMember(memberId);
        MemberSettings memberSettings = getMemberSettings(member.getId());

        memberSettings.updateSettings(
                request.pushNotification(),
                request.emailNotification(),
                request.autoSave()
        );

        return MemberConverter.toSettingsResultDTO(memberSettings);
    }

    public void changePassword(Long memberId, MemberRequestDTO.ChangePasswordDTO request) {
        Member member = getActiveMember(memberId);

        if (!passwordEncoder.matches(request.currentPassword(), member.getPassword())) {
            throw new MemberException(MemberErrorCode.INVALID_CURRENT_PASSWORD);
        }

        validatePasswordConfirm(request.newPassword(), request.newPasswordConfirm());

        member.changePassword(passwordEncoder.encode(request.newPassword()));
    }

    private void validatePasswordConfirm(String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            throw new MemberException(MemberErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }
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

    private Member getActiveMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
        }

        return member;
    }

    private void validateProfileImageKey(Long memberId, String objectKey) {
        String memberProfilePrefix = "profiles/%d/".formatted(memberId);
        if (!objectKey.startsWith(memberProfilePrefix)) {
            throw new StorageException(StorageErrorCode.INVALID_OBJECT_KEY);
        }
    }

    private MemberSettings getMemberSettings(Long memberId) {
        return memberSettingsRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_SETTINGS_NOT_FOUND));
    }
}
