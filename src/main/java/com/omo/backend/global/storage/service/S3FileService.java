package com.omo.backend.global.storage.service;

import com.omo.backend.global.storage.exception.StorageErrorCode;
import com.omo.backend.global.storage.exception.StorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
public class S3FileService {

    private final S3Client s3Client;

    public ObjectMetadata getObjectMetadata(String bucket, String objectKey) {
        try {
            // 파일 본문을 내려받지 않고 S3 객체의 Content-Type과 실제 크기만 조회
            HeadObjectResponse response = s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .build());
            return new ObjectMetadata(response.contentType(), response.contentLength());
        } catch (S3Exception exception) {
            if (exception.statusCode() == 404) {
                throw new StorageException(StorageErrorCode.FILE_NOT_FOUND);
            }
            throw new StorageException(StorageErrorCode.S3_OPERATION_FAILED);
        } catch (SdkException exception) {
            throw new StorageException(StorageErrorCode.S3_OPERATION_FAILED);
        }
    }

    public void delete(String bucket, String objectKey) {
        try {
            // DB에서 더 이상 참조하지 않는 기존 프로필 이미지 객체를 삭제
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .build());
        } catch (SdkException exception) {
            throw new StorageException(StorageErrorCode.S3_OPERATION_FAILED);
        }
    }

    public void upload(String bucket, String objectKey, String contentType, byte[] content) {
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(objectKey)
                            .contentType(contentType)
                            .contentLength((long) content.length)
                            .build(),
                    RequestBody.fromBytes(content)
            );
        } catch (SdkException exception) {
            throw new StorageException(StorageErrorCode.S3_OPERATION_FAILED);
        }
    }

    public void copy(String bucket, String sourceObjectKey, String destinationObjectKey) {
        try {
            s3Client.copyObject(CopyObjectRequest.builder()
                    .sourceBucket(bucket)
                    .sourceKey(sourceObjectKey)
                    .destinationBucket(bucket)
                    .destinationKey(destinationObjectKey)
                    .build());
        } catch (SdkException exception) {
            throw new StorageException(StorageErrorCode.S3_OPERATION_FAILED);
        }
    }

    public record ObjectMetadata(
            String contentType,
            long contentLength
    ) {}
}
