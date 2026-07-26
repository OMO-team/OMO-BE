package com.omo.backend.global.storage.enums;

import com.omo.backend.global.storage.exception.StorageErrorCode;
import com.omo.backend.global.storage.exception.StorageException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum FileType {

    JPEG(Set.of("jpg", "jpeg"), "image/jpeg", "jpg"),
    PNG(Set.of("png"), "image/png", "png"),
    WEBP(Set.of("webp"), "image/webp", "webp");

    private final Set<String> extensions;
    private final String contentType;
    private final String canonicalExtension;

    public static FileType from(String fileName, String contentType) {
        String extension = extractExtension(fileName);
        String normalizedContentType = normalizeContentType(contentType);

        return Arrays.stream(values())
                .filter(fileType -> fileType.extensions.contains(extension))
                .filter(fileType -> fileType.contentType.equals(normalizedContentType))
                .findFirst()
                .orElseThrow(() -> new StorageException(StorageErrorCode.UNSUPPORTED_FILE_TYPE));
    }

    private static String extractExtension(String fileName) {
        if (fileName == null) {
            throw new StorageException(StorageErrorCode.UNSUPPORTED_FILE_TYPE);
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex < 0 || lastDotIndex == fileName.length() - 1) {
            throw new StorageException(StorageErrorCode.UNSUPPORTED_FILE_TYPE);
        }

        return fileName.substring(lastDotIndex + 1).toLowerCase(Locale.ROOT);
    }

    private static String normalizeContentType(String contentType) {
        if (contentType == null) {
            throw new StorageException(StorageErrorCode.UNSUPPORTED_FILE_TYPE);
        }

        return contentType.split(";", 2)[0].trim().toLowerCase(Locale.ROOT);
    }
}
