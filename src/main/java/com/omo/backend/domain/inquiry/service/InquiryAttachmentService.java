package com.omo.backend.domain.inquiry.service;

import com.omo.backend.domain.inquiry.entity.Inquiry;
import com.omo.backend.domain.inquiry.entity.InquiryAttachment;
import com.omo.backend.domain.inquiry.exception.InquiryErrorCode;
import com.omo.backend.domain.inquiry.exception.InquiryException;
import com.omo.backend.domain.inquiry.repository.InquiryAttachmentRepository;
import com.omo.backend.global.storage.FileValidationPolicy;
import com.omo.backend.global.storage.S3Properties;
import com.omo.backend.global.storage.service.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InquiryAttachmentService {

    private final InquiryAttachmentRepository inquiryAttachmentRepository;
    private final InquiryUploadSessionStore inquiryUploadSessionStore;
    private final S3FileService s3FileService;
    private final FileValidationPolicy fileValidationPolicy;
    private final S3Properties s3Properties;

    public void saveAttachments(Inquiry inquiry, String uploadToken, List<String> attachmentKeys) {
        // 첨부파일이 없는 문의는 S3 및 Redis 처리 없이 종료 (단, 파일 없이 토큰만 전달한 비정상 요청은 예외 처리)
        if (isEmptyAttachmentRequest(uploadToken, attachmentKeys)) {
            return;
        }

        // 잘못된 요청이 유효한 토큰을 불필요하게 선점하지 않도록 기본 형식을 먼저 검증
        validateAttachmentRequest(uploadToken, attachmentKeys);

        // Redis Lua Script로 READY 상태의 토큰을 PROCESSING 상태로 원자적으로 선점 (동일 토큰을 사용한 동시 요청 중 하나만 이후 첨부파일 처리를 진행할 수 있음)
        claimUploadToken(uploadToken);

        try {
            // Redis 발급 내역과 S3 메타데이터를 모두 검증한 뒤 객체 이동 시작
            List<ValidatedAttachment> validatedAttachments = attachmentKeys.stream()
                    .map(objectKey -> validateAttachment(uploadToken, objectKey))
                    .toList();

            List<InquiryAttachment> attachments = validatedAttachments.stream()
                    .map(attachment -> moveAndCreateAttachment(inquiry, attachment))
                    .toList();

            // 문의와 첨부파일 연결을 저장한 뒤 업로드 토큰을 삭제해 재사용 방지
            inquiryAttachmentRepository.saveAll(attachments);
            inquiryUploadSessionStore.consume(uploadToken);
        } catch (RuntimeException exception) {
            // 처리에 실패한 토큰은 다시 시도할 수 있도록 선점을 해제
            inquiryUploadSessionStore.release(uploadToken);
            throw exception;
        }
    }

    private boolean isEmptyAttachmentRequest(String uploadToken, List<String> attachmentKeys) {
        // attachmentKeys가 없으면 uploadToken도 없어야 정상적인 첨부파일 미포함 요청
        if (attachmentKeys == null || attachmentKeys.isEmpty()) {
            if (uploadToken != null && !uploadToken.isBlank()) {
                throw new InquiryException(InquiryErrorCode.INVALID_ATTACHMENT_REQUEST);
            }
            return true;
        }
        return false;
    }

    private void validateAttachmentRequest(String uploadToken, List<String> attachmentKeys) {
        // 첨부파일이 있으면 업로드 URL 발급 시 함께 반환된 토큰이 반드시 필요
        if (uploadToken == null || uploadToken.isBlank()) {
            throw new InquiryException(InquiryErrorCode.INVALID_ATTACHMENT_REQUEST);
        }

        // 하나의 문의에 동일한 object key가 중복 연결되는 것을 방지
        if (new HashSet<>(attachmentKeys).size() != attachmentKeys.size()) {
            throw new InquiryException(InquiryErrorCode.DUPLICATE_ATTACHMENT);
        }

        // 다른 업로드 세션의 object key를 현재 문의에 연결하지 못하도록 토큰별 임시 경로 확인
        String expectedPrefix = "temp/inquiries/%s/".formatted(uploadToken);
        if (attachmentKeys.stream().anyMatch(objectKey -> !objectKey.startsWith(expectedPrefix))) {
            throw new InquiryException(InquiryErrorCode.INVALID_UPLOAD_TOKEN);
        }
    }

    private void claimUploadToken(String uploadToken) {
        InquiryUploadSessionStore.ClaimResult claimResult = inquiryUploadSessionStore.claim(uploadToken);

        // Redis에 키가 없으면 발급되지 않았거나 유효시간이 만료된 토큰
        if (claimResult == InquiryUploadSessionStore.ClaimResult.NOT_FOUND) {
            throw new InquiryException(InquiryErrorCode.INVALID_UPLOAD_TOKEN);
        }

        // 이미 PROCESSING 상태이면 다른 요청이 같은 토큰으로 첨부파일을 처리 중
        if (claimResult == InquiryUploadSessionStore.ClaimResult.ALREADY_CLAIMED) {
            throw new InquiryException(InquiryErrorCode.UPLOAD_TOKEN_IN_USE);
        }
    }

    private ValidatedAttachment validateAttachment(String uploadToken, String objectKey) {
        // 경로가 올바르더라도 임의로 조작한 key일 수 있으므로 Redis 발급 내역까지 확인 (검증과 함께 DB에 저장할 사용자의 원본 파일명을 조회)
        String originalName = inquiryUploadSessionStore.findOriginalName(uploadToken, objectKey)
                .orElseThrow(() -> new InquiryException(InquiryErrorCode.INVALID_UPLOAD_TOKEN));

        // Presigned URL만 발급받고 업로드하지 않은 경우를 차단하고, 클라이언트 요청값이 아닌 실제 S3 객체의 Content-Type과 크기를 다시 검증
        S3FileService.ObjectMetadata metadata = s3FileService.getObjectMetadata(s3Properties.inquiryBucket(), objectKey);
        fileValidationPolicy.validateStoredObject(objectKey, metadata.contentType(), metadata.contentLength());

        return new ValidatedAttachment(originalName, objectKey, metadata.contentType(), metadata.contentLength());
    }

    private InquiryAttachment moveAndCreateAttachment(Inquiry inquiry, ValidatedAttachment attachment) {
        // 문의 ID 기반 영구 object key를 생성하고 임시 객체를 영구 경로로 이동
        String destinationObjectKey = generatePermanentObjectKey(inquiry.getId(), attachment.objectKey());
        s3FileService.move(s3Properties.inquiryBucket(), attachment.objectKey(), destinationObjectKey);

        // 이동이 완료된 영구 object key와 S3 메타데이터로 첨부파일 엔티티 생성
        return InquiryAttachment.createInquiryAttachment(
                inquiry,
                attachment.originalName(),
                extractStoredName(destinationObjectKey),
                destinationObjectKey,
                attachment.contentType(),
                attachment.fileSize()
        );
    }

    private String generatePermanentObjectKey(Long inquiryId, String tempObjectKey) {
        // 임시 파일의 확장자를 유지해 inquiries/{inquiryId}/{uuid}.{extension} 형식으로 생성
        String extension = tempObjectKey.substring(tempObjectKey.lastIndexOf('.'));
        return "inquiries/%d/%s%s".formatted(inquiryId, UUID.randomUUID(), extension);
    }

    private String extractStoredName(String objectKey) {
        // object key의 마지막 경로에서 실제 S3 저장 파일명 추출
        return objectKey.substring(objectKey.lastIndexOf('/') + 1);
    }

    private record ValidatedAttachment(
            String originalName,
            String objectKey,
            String contentType,
            long fileSize
    ) {}
}
