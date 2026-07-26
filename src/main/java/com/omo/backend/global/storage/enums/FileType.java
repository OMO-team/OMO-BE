package com.omo.backend.global.storage.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
}
