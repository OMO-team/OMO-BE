package com.omo.backend.global.storage.service;

import com.omo.backend.global.storage.S3Properties;
import com.omo.backend.global.storage.exception.StorageErrorCode;
import com.omo.backend.global.storage.exception.StorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class S3PresignedUrlService {

    private final S3Presigner s3Presigner;
    private final S3Properties properties;

    public PresignedPutUrl createPutUrl(String bucket, String objectKey, String contentType) {
        // 업로드를 허용할 버킷, object key, Content-Type을 고정
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .contentType(contentType)
                .build();

        // 위 PUT 요청에 환경설정의 유효시간을 적용해 서명 요청 생성
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(properties.presignedUrlExpiration())
                .putObjectRequest(putObjectRequest)
                .build();

        try {
            // IAM 자격 증명으로 서명된 URL을 생성 (실제 파일 업로드는 프론트가 수행)
            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

            // 프론트가 URL 만료를 판단할 수 있도록 절대 만료시각도 함께 반환
            Instant expiresAt = Instant.now().plus(properties.presignedUrlExpiration());
            return new PresignedPutUrl(presignedRequest.url(), expiresAt);
        } catch (SdkException exception) {
            throw new StorageException(StorageErrorCode.PRESIGNED_URL_GENERATION_FAILED);
        }
    }

    public PresignedGetUrl createGetUrl(String bucket, String objectKey) {
        // 조회할 버킷과 object key를 고정
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();

        // 인증된 회원에게 제한된 시간 동안만 유효한 조회 URL을 발급
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(properties.presignedUrlExpiration())
                .getObjectRequest(getObjectRequest)
                .build();

        try {
            // IAM 자격 증명으로 서명된 URL을 조회
            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

            // 프론트가 URL 만료를 판단할 수 있도록 절대 만료시각도 함께 반환
            Instant expiresAt = Instant.now().plus(properties.presignedUrlExpiration());
            return new PresignedGetUrl(presignedRequest.url(), expiresAt);
        } catch (SdkException exception) {
            throw new StorageException(StorageErrorCode.PRESIGNED_URL_GENERATION_FAILED);
        }
    }

    public record PresignedPutUrl(
            URL uploadUrl,
            Instant expiresAt
    ) {}

    public record PresignedGetUrl(
            URL imageUrl,
            Instant expiresAt
    ) {}
}
