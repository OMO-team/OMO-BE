package com.omo.backend.domain.inquiry.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InquiryUploadSessionStore {

    private static final String UPLOAD_SESSION_KEY_PREFIX = "inquiry:upload:";
    private static final String STATUS_FIELD = "__status";
    private static final String READY = "READY";
    private static final String PROCESSING = "PROCESSING";

    // 기존 키 초기화 -> 상태를 READY로 저장 -> objectKey와 원본 파일명 저장 -> 만료 시간 설정
    private static final DefaultRedisScript<Long> SAVE_SCRIPT = new DefaultRedisScript<>("""
            redis.call('DEL', KEYS[1])
            redis.call('HSET', KEYS[1], ARGV[1], ARGV[2])
            for index = 4, #ARGV, 2 do
                redis.call('HSET', KEYS[1], ARGV[index], ARGV[index + 1])
            end
            redis.call('PEXPIRE', KEYS[1], ARGV[3])
            return 1
            """, Long.class);

    // READY -> PROCESSING (처리 가능한 토큰만 원자적으로 선점)
    private static final DefaultRedisScript<Long> CLAIM_SCRIPT = new DefaultRedisScript<>("""
            if redis.call('EXISTS', KEYS[1]) == 0 then
                return -1
            end
            local status = redis.call('HGET', KEYS[1], ARGV[1])
            if status and status ~= ARGV[2] then
                return 0
            end
            redis.call('HSET', KEYS[1], ARGV[1], ARGV[3])
            return 1
            """, Long.class);

    // PROCESSING -> READY (첨부파일 처리에 실패하면 재시도할 수 있도록 선점 해제)
    private static final DefaultRedisScript<Long> RELEASE_SCRIPT = new DefaultRedisScript<>("""
            if redis.call('HGET', KEYS[1], ARGV[1]) ~= ARGV[2] then
                return 0
            end
            redis.call('HSET', KEYS[1], ARGV[1], ARGV[3])
            return 1
            """, Long.class);

    // PROCESSING 상태 확인 -> Redis 키 삭제 (처리에 성공한 토큰을 제거해 재사용 차단)
    private static final DefaultRedisScript<Long> CONSUME_SCRIPT = new DefaultRedisScript<>("""
            if redis.call('HGET', KEYS[1], ARGV[1]) ~= ARGV[2] then
                return 0
            end
            redis.call('DEL', KEYS[1])
            return 1
            """, Long.class);

    private final StringRedisTemplate redisTemplate;

    /**
     * Presigned URL 발급 정보를 새로운 업로드 세션으로 저장
     * 세션을 READY 상태로 생성하고 objectKey별 원본 파일명과 TTL을 함께 설정
     * 저장과 만료 시간 설정을 하나의 Lua Script로 실행해 TTL이 없는 세션이 남는 것을 방지
     */
    public void save(String uploadToken, Map<String, String> originalNamesByObjectKey, Duration expiration) {
        String redisKey = UPLOAD_SESSION_KEY_PREFIX + uploadToken;
        List<String> arguments = new ArrayList<>(3 + originalNamesByObjectKey.size() * 2);
        arguments.add(STATUS_FIELD);
        arguments.add(READY);
        arguments.add(String.valueOf(expiration.toMillis()));
        originalNamesByObjectKey.forEach((objectKey, originalName) -> {
            arguments.add(objectKey);
            arguments.add(originalName);
        });

        redisTemplate.execute(SAVE_SCRIPT, List.of(redisKey), arguments.toArray());
    }

    /**
     * 문의 등록을 시작한 요청이 업로드 토큰을 원자적으로 선점
     * READY 상태인 토큰만 PROCESSING으로 변경되며, 동시 요청 중 하나만 CLAIMED를 반환받음
     * 토큰이 없거나 만료된 경우와 다른 요청이 이미 선점한 경우를 구분해 반환함
     */
    public ClaimResult claim(String uploadToken) {
        Long result = redisTemplate.execute(
                CLAIM_SCRIPT,
                List.of(redisKey(uploadToken)),
                STATUS_FIELD,
                READY,
                PROCESSING
        );

        if (Long.valueOf(1L).equals(result)) {
            return ClaimResult.CLAIMED;
        }
        if (Long.valueOf(-1L).equals(result)) {
            return ClaimResult.NOT_FOUND;
        }
        return ClaimResult.ALREADY_CLAIMED;
    }

    public Optional<String> findOriginalName(String uploadToken, String objectKey) {
        Object originalName = redisTemplate.opsForHash().get(redisKey(uploadToken), objectKey);

        // 토큰이 만료됐거나 발급되지 않은 object key이면 Optional.empty()를 반환
        return Optional.ofNullable(originalName).map(Object::toString);
    }

    /**
     * 첨부파일 처리에 실패한 요청의 선점을 해제해 같은 토큰으로 재시도할 수 있게 함
     * 현재 상태가 PROCESSING일 때만 READY로 되돌리며, 상태가 다르거나 토큰이 없으면 false를 반환
     */
    public boolean release(String uploadToken) {
        Long result = redisTemplate.execute(
                RELEASE_SCRIPT,
                List.of(redisKey(uploadToken)),
                STATUS_FIELD,
                PROCESSING,
                READY
        );
        return Long.valueOf(1L).equals(result);
    }

    /**
     * 첨부파일 처리를 완료한 업로드 토큰을 소비해 재사용을 차단
     * 현재 상태가 PROCESSING일 때만 Redis 세션 전체를 삭제하며, 삭제하지 못하면 false를 반환함
     */
    public boolean consume(String uploadToken) {
        Long result = redisTemplate.execute(
                CONSUME_SCRIPT,
                List.of(redisKey(uploadToken)),
                STATUS_FIELD,
                PROCESSING
        );
        return Long.valueOf(1L).equals(result);
    }

    private String redisKey(String uploadToken) {
        return UPLOAD_SESSION_KEY_PREFIX + uploadToken;
    }

    public enum ClaimResult {
        CLAIMED,         // 현재 요청이 토큰 선점에 성공
        NOT_FOUND,       // 토큰이 없거나 만료됨
        ALREADY_CLAIMED  // 다른 요청이 이미 처리 중
    }
}
