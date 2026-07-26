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

@Service
@RequiredArgsConstructor
public class InquiryAttachmentService {

    private final InquiryAttachmentRepository inquiryAttachmentRepository;
    private final InquiryUploadSessionStore inquiryUploadSessionStore;
    private final S3FileService s3FileService;
    private final FileValidationPolicy fileValidationPolicy;
    private final S3Properties s3Properties;

    public void saveAttachments(Inquiry inquiry, String uploadToken, List<String> attachmentKeys) {
        List<ValidatedAttachment> validatedAttachments = validateAttachments(uploadToken, attachmentKeys);
        if (validatedAttachments.isEmpty()) {
            return;
        }

        List<InquiryAttachment> attachments = validatedAttachments.stream()
                .map(attachment -> InquiryAttachment.createInquiryAttachment(
                        inquiry,
                        attachment.originalName(),
                        extractStoredName(attachment.objectKey()),
                        attachment.objectKey(),
                        attachment.contentType(),
                        attachment.fileSize()
                ))
                .toList();

        inquiryAttachmentRepository.saveAll(attachments);
        inquiryUploadSessionStore.delete(uploadToken);
    }

    private List<ValidatedAttachment> validateAttachments(String uploadToken, List<String> attachmentKeys) {
        if (attachmentKeys == null || attachmentKeys.isEmpty()) {
            if (uploadToken != null && !uploadToken.isBlank()) {
                throw new InquiryException(InquiryErrorCode.INVALID_ATTACHMENT_REQUEST);
            }
            return List.of();
        }

        if (uploadToken == null || uploadToken.isBlank()) {
            throw new InquiryException(InquiryErrorCode.INVALID_ATTACHMENT_REQUEST);
        }
        if (new HashSet<>(attachmentKeys).size() != attachmentKeys.size()) {
            throw new InquiryException(InquiryErrorCode.DUPLICATE_ATTACHMENT);
        }

        String expectedPrefix = "temp/inquiries/%s/".formatted(uploadToken);
        return attachmentKeys.stream()
                .map(objectKey -> validateAttachment(uploadToken, expectedPrefix, objectKey))
                .toList();
    }

    private ValidatedAttachment validateAttachment(
            String uploadToken,
            String expectedPrefix,
            String objectKey
    ) {
        if (!objectKey.startsWith(expectedPrefix)) {
            throw new InquiryException(InquiryErrorCode.INVALID_UPLOAD_TOKEN);
        }

        String originalName = inquiryUploadSessionStore.findOriginalName(uploadToken, objectKey)
                .orElseThrow(() -> new InquiryException(InquiryErrorCode.INVALID_UPLOAD_TOKEN));
        S3FileService.ObjectMetadata metadata =
                s3FileService.getObjectMetadata(s3Properties.inquiryBucket(), objectKey);
        fileValidationPolicy.validateStoredObject(
                objectKey,
                metadata.contentType(),
                metadata.contentLength()
        );

        return new ValidatedAttachment(
                originalName,
                objectKey,
                metadata.contentType(),
                metadata.contentLength()
        );
    }

    private String extractStoredName(String objectKey) {
        return objectKey.substring(objectKey.lastIndexOf('/') + 1);
    }

    private record ValidatedAttachment(
            String originalName,
            String objectKey,
            String contentType,
            long fileSize
    ) {}
}
