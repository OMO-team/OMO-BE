package com.omo.backend.domain.inquiry.service;

import com.omo.backend.domain.inquiry.exception.InquiryErrorCode;
import com.omo.backend.domain.inquiry.exception.InquiryException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InquiryUploadRateLimiter {

    private static final String RATE_LIMIT_KEY_PREFIX = "rate-limit:inquiry-upload:";
    private static final long MAX_REQUESTS = 5L;
    private static final Duration WINDOW = Duration.ofMinutes(1);

    // 요청 횟수 증가와 첫 요청의 TTL 설정을 하나의 원자적 작업으로 처리
    private static final DefaultRedisScript<Long> INCREMENT_SCRIPT = new DefaultRedisScript<>("""
            local requestCount = redis.call('INCR', KEYS[1])
            if requestCount == 1 then
                redis.call('PEXPIRE', KEYS[1], ARGV[1])
            end
            return requestCount
            """, Long.class);

    private final StringRedisTemplate redisTemplate;

    public void check(String clientIp) {
        Long requestCount = redisTemplate.execute(INCREMENT_SCRIPT, List.of(RATE_LIMIT_KEY_PREFIX + clientIp), String.valueOf(WINDOW.toMillis()));

        if (requestCount == null) {
            throw new IllegalStateException("문의 이미지 업로드 요청 횟수를 확인하지 못했습니다.");
        }

        if (requestCount > MAX_REQUESTS) {
            throw new InquiryException(InquiryErrorCode.UPLOAD_RATE_LIMIT_EXCEEDED);
        }
    }
}
