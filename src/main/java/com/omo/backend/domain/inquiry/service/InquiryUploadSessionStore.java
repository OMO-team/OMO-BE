package com.omo.backend.domain.inquiry.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InquiryUploadSessionStore {

    private static final String UPLOAD_SESSION_KEY_PREFIX = "inquiry:upload:";

    private final StringRedisTemplate redisTemplate;

    public void save(String uploadToken, List<String> objectKeys, Duration expiration) {
        String redisKey = UPLOAD_SESSION_KEY_PREFIX + uploadToken;
        redisTemplate.opsForSet().add(redisKey, objectKeys.toArray(String[]::new));
        redisTemplate.expire(redisKey, expiration);
    }
}
