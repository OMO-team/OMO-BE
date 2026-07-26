package com.omo.backend.global.storage;

import com.omo.backend.global.storage.enums.FileType;
import com.omo.backend.global.storage.exception.StorageErrorCode;
import com.omo.backend.global.storage.exception.StorageException;
import org.springframework.stereotype.Component;

@Component
public class FileValidationPolicy {

    public static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024;  // 5MB

    public FileType validateUploadRequest(String fileName, String contentType, long fileSize) {
        validateFileSize(fileSize);
        return FileType.from(fileName, contentType);
    }

    public void validateStoredObject(String objectKey, String contentType, long fileSize) {
        validateFileSize(fileSize);
        FileType.from(objectKey, contentType);
    }

    private void validateFileSize(long fileSize) {
        if (fileSize <= 0 || fileSize > MAX_FILE_SIZE_BYTES) {
            throw new StorageException(StorageErrorCode.INVALID_FILE_SIZE);
        }
    }
}
