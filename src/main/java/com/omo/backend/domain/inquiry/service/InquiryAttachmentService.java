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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
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

        List<PreparedAttachment> preparedAttachments = new ArrayList<>(attachmentKeys.size());
        try {
            // Redis 발급 내역과 S3 메타데이터를 모두 검증한 뒤 영구 객체 복사 시작
            List<ValidatedAttachment> validatedAttachments = attachmentKeys.stream()
                    .map(objectKey -> validateAttachment(uploadToken, objectKey))
                    .toList();

            for (ValidatedAttachment attachment : validatedAttachments) {
                // S3가 복사를 완료한 뒤 응답 과정에서 예외가 발생해도 정리할 수 있도록 목적지 키를 먼저 기록
                PreparedAttachment preparedAttachment = prepareAttachment(inquiry, attachment);
                preparedAttachments.add(preparedAttachment);
                s3FileService.copy(s3Properties.inquiryBucket(), preparedAttachment.sourceObjectKey(), preparedAttachment.destinationObjectKey());
            }

            inquiryAttachmentRepository.saveAll(preparedAttachments.stream().map(PreparedAttachment::entity).toList());

            // DB 트랜잭션 결과가 확정된 뒤 임시/영구 객체 정리와 토큰 상태 변경 수행
            registerTransactionCompletion(inquiry.getId(), uploadToken, preparedAttachments);
        } catch (RuntimeException exception) {
            // 트랜잭션 동기화 등록 전 실패하면 이미 복사한 영구 객체를 즉시 보상 삭제
            compensateRollback(inquiry.getId(), uploadToken, preparedAttachments);
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

    private PreparedAttachment prepareAttachment(Inquiry inquiry, ValidatedAttachment attachment) {
        // 복사 전에 영구 object key와 저장할 엔티티를 준비해 실패 시 보상 삭제 대상을 추적
        String destinationObjectKey = generatePermanentObjectKey(inquiry.getId(), attachment.objectKey());
        InquiryAttachment entity = InquiryAttachment.createInquiryAttachment(
                inquiry,
                attachment.originalName(),
                extractStoredName(destinationObjectKey),
                destinationObjectKey,
                attachment.contentType(),
                attachment.fileSize()
        );
        return new PreparedAttachment(attachment.objectKey(), destinationObjectKey, entity);
    }

    private void registerTransactionCompletion(Long inquiryId, String uploadToken, List<PreparedAttachment> preparedAttachments) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            // 트랜잭션 없이 호출된 경우 repository 저장 성공을 커밋 성공과 동일하게 처리
            completeCommit(inquiryId, uploadToken, preparedAttachments);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == TransactionSynchronization.STATUS_COMMITTED) {
                    completeCommit(inquiryId, uploadToken, preparedAttachments);
                    return;
                }

                if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
                    compensateRollback(inquiryId, uploadToken, preparedAttachments);
                    return;
                }

                handleUnknownCompletion(inquiryId, uploadToken, preparedAttachments);
            }
        });
    }

    private void completeCommit(Long inquiryId, String uploadToken, List<PreparedAttachment> preparedAttachments) {
        // DB에 영구 object key 저장이 확정된 뒤 더 이상 필요하지 않은 임시 객체 삭제
        preparedAttachments.forEach(attachment -> deleteWithLog(inquiryId, attachment.sourceObjectKey(), "임시 객체 정리"));

        // 임시 객체 삭제 실패 여부와 관계없이 토큰은 소비해 중복 문의 등록 차단
        try {
            if (!inquiryUploadSessionStore.consume(uploadToken)) {
                log.warn("문의 첨부파일 처리 완료 후 업로드 토큰을 소비하지 못했습니다. inquiryId={}, uploadToken={}", inquiryId, uploadToken);
            }
        } catch (RuntimeException exception) {
            log.error("문의 첨부파일 처리 완료 후 업로드 토큰 소비 중 오류가 발생했습니다. inquiryId={}, uploadToken={}", inquiryId, uploadToken, exception);
        }
    }

    private void compensateRollback(Long inquiryId, String uploadToken, List<PreparedAttachment> preparedAttachments) {
        // DB 롤백 또는 일부 복사 실패 시 DB에서 참조되지 않을 영구 객체를 보상 삭제
        preparedAttachments.forEach(attachment -> deleteWithLog(inquiryId, attachment.destinationObjectKey(), "영구 객체 보상 삭제"));

        // 임시 객체는 그대로 남아 있으므로 토큰 선점을 해제해 제한 시간 내 재시도 허용
        try {
            if (!inquiryUploadSessionStore.release(uploadToken)) {
                log.warn("문의 첨부파일 처리 실패 후 업로드 토큰 선점을 해제하지 못했습니다. inquiryId={}, uploadToken={}", inquiryId, uploadToken);
            }
        } catch (RuntimeException exception) {
            log.error("문의 첨부파일 처리 실패 후 업로드 토큰 선점 해제 중 오류가 발생했습니다. inquiryId={}, uploadToken={}", inquiryId, uploadToken, exception);
        }
    }

    private void handleUnknownCompletion(Long inquiryId, String uploadToken, List<PreparedAttachment> preparedAttachments) {
        // DB 커밋 여부를 알 수 없으므로 임시·영구 객체와 토큰을 변경하지 않음 (DB 상태를 확인한 뒤 안전하게 정리할 수 있도록 두 object key를 모두 기록)
        preparedAttachments.forEach(attachment -> log.error(
                "문의 첨부파일 트랜잭션 결과를 확인할 수 없습니다. inquiryId={}, uploadToken={}, sourceObjectKey={}, destinationObjectKey={}",
                inquiryId, uploadToken, attachment.sourceObjectKey(), attachment.destinationObjectKey()
        ));
    }

    private void deleteWithLog(Long inquiryId, String objectKey, String operation) {
        try {
            s3FileService.delete(s3Properties.inquiryBucket(), objectKey);
        } catch (RuntimeException exception) {
            // 이 시점에는 DB 커밋 또는 롤백 결과가 이미 결정되어 삭제 실패를 다시 되돌릴 수 없음 (기존 처리 결과는 유지하고, 나중에 남은 S3 객체를 확인하고 정리할 수 있도록 로그를 기록)
            log.error("문의 첨부파일 {}에 실패했습니다. inquiryId={}, objectKey={}", operation, inquiryId, objectKey, exception);
        }
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

    private record PreparedAttachment(
            String sourceObjectKey,
            String destinationObjectKey,
            InquiryAttachment entity
    ) {}
}
