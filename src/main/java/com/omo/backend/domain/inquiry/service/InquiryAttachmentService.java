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
        // 업로드 토큰, object key 경로, Redis 발급 내역 및 S3 메타데이터를 검증
        List<ValidatedAttachment> validatedAttachments = validateAttachments(uploadToken, attachmentKeys);
        if (validatedAttachments.isEmpty()) {
            return;
        }

        // 검증된 임시 객체를 문의별 영구 경로로 이동하고 첨부파일 엔티티 생성
        List<InquiryAttachment> attachments = validatedAttachments.stream()
                .map(attachment -> moveAndCreateAttachment(inquiry, attachment))
                .toList();

        // 문의와 첨부파일 연결을 저장한 뒤 업로드 토큰을 삭제해 재사용 방지
        inquiryAttachmentRepository.saveAll(attachments);
        inquiryUploadSessionStore.delete(uploadToken);
    }

    private List<ValidatedAttachment> validateAttachments(String uploadToken, List<String> attachmentKeys) {
        // 첨부파일이 없으면 uploadToken도 전달되지 않은 요청만 허용
        if (attachmentKeys == null || attachmentKeys.isEmpty()) {
            if (uploadToken != null && !uploadToken.isBlank()) {
                throw new InquiryException(InquiryErrorCode.INVALID_ATTACHMENT_REQUEST);
            }
            return List.of();
        }

        // 첨부파일이 있으면 업로드 URL 발급 시 받은 uploadToken이 반드시 필요
        if (uploadToken == null || uploadToken.isBlank()) {
            throw new InquiryException(InquiryErrorCode.INVALID_ATTACHMENT_REQUEST);
        }

        // 하나의 문의에 동일한 object key가 중복 연결되는 것을 방지
        if (new HashSet<>(attachmentKeys).size() != attachmentKeys.size()) {
            throw new InquiryException(InquiryErrorCode.DUPLICATE_ATTACHMENT);
        }

        // 전달된 모든 key가 해당 uploadToken의 임시 경로와 발급 내역에 속하는지 검증
        String expectedPrefix = "temp/inquiries/%s/".formatted(uploadToken);
        return attachmentKeys.stream()
                .map(objectKey -> validateAttachment(uploadToken, expectedPrefix, objectKey))
                .toList();
    }

    private ValidatedAttachment validateAttachment(String uploadToken, String expectedPrefix, String objectKey) {
        // 다른 업로드 세션의 임시 경로나 임의로 만든 object key를 차단
        if (!objectKey.startsWith(expectedPrefix)) {
            throw new InquiryException(InquiryErrorCode.INVALID_UPLOAD_TOKEN);
        }

        // Redis에서 실제 발급한 object key인지 확인하고 DB에 저장할 원본 파일명 조회
        String originalName = inquiryUploadSessionStore.findOriginalName(uploadToken, objectKey)
                .orElseThrow(() -> new InquiryException(InquiryErrorCode.INVALID_UPLOAD_TOKEN));

        // S3 객체의 실제 존재 여부, Content-Type 및 파일 크기를 다시 검증
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
