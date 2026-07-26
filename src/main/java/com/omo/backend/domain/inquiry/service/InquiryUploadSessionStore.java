package com.omo.backend.domain.inquiry.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InquiryUploadSessionStore {

    private static final String UPLOAD_SESSION_KEY_PREFIX = "inquiry:upload:";

    private final StringRedisTemplate redisTemplate;

    public void save(String uploadToken, Map<String, String> originalNamesByObjectKey, Duration expiration) {
        String redisKey = UPLOAD_SESSION_KEY_PREFIX + uploadToken;
        redisTemplate.opsForHash().putAll(redisKey, originalNamesByObjectKey);
        redisTemplate.expire(redisKey, expiration);
    }

    public Optional<String> findOriginalName(String uploadToken, String objectKey) {
        Object originalName = redisTemplate.opsForHash().get(UPLOAD_SESSION_KEY_PREFIX + uploadToken, objectKey);

        // 토큰이 만료됐거나 발급되지 않은 object key이면 Optional.empty()를 반환
        return Optional.ofNullable(originalName).map(Object::toString);
    }

    public void delete(String uploadToken) {
        redisTemplate.delete(UPLOAD_SESSION_KEY_PREFIX + uploadToken);
    }
}
