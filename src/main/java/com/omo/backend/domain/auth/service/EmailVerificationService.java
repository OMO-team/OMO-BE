package com.omo.backend.domain.auth.service;

import com.omo.backend.domain.auth.converter.AuthConverter;
import com.omo.backend.domain.auth.dto.AuthRequestDTO;
import com.omo.backend.domain.auth.dto.AuthResponseDTO;
import com.omo.backend.domain.auth.exception.AuthErrorCode;
import com.omo.backend.domain.auth.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final Duration CODE_TTL = Duration.ofMinutes(5);
    private static final Duration VERIFIED_TTL = Duration.ofMinutes(30);
    private static final int MAX_VERIFY_ATTEMPTS = 5;
    private static final String CODE_KEY_PREFIX = "auth:email:code:";
    private static final String VERIFIED_KEY_PREFIX = "auth:email:verified:";
    private static final String ATTEMPT_KEY_PREFIX = "auth:email:attempt:";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;

    // 이메일 인증번호 발송
    public AuthResponseDTO.EmailSendResultDTO send(AuthRequestDTO.EmailSendDTO request) {
        String code = generateCode();

        redisTemplate.opsForValue().set(codeKey(request.email()), code, CODE_TTL);
        redisTemplate.delete(verifiedKey(request.email()));
        redisTemplate.delete(attemptKey(request.email()));

        sendEmail(request.email(), code);

        return AuthConverter.toEmailSendResultDTO(request.email(), CODE_TTL.toSeconds());
    }

    // 이메일 인증번호 발송 검증
    public AuthResponseDTO.EmailVerifyResultDTO verify(AuthRequestDTO.EmailVerifyDTO request) {
        String savedCode = redisTemplate.opsForValue().get(codeKey(request.email()));

        if (savedCode == null) {
            throw new AuthException(AuthErrorCode.EMAIL_VERIFICATION_CODE_NOT_FOUND);
        }

        if (isAttemptExceeded(request.email())) {
            throw new AuthException(AuthErrorCode.EMAIL_VERIFICATION_ATTEMPT_EXCEEDED);
        }

        if (!savedCode.equals(request.code())) {
            if (increaseAttemptAndIsExceeded(request.email())) {
                throw new AuthException(AuthErrorCode.EMAIL_VERIFICATION_ATTEMPT_EXCEEDED);
            }
            throw new AuthException(AuthErrorCode.EMAIL_VERIFICATION_CODE_MISMATCH);
        }

        redisTemplate.opsForValue().set(verifiedKey(request.email()), "true", VERIFIED_TTL);
        redisTemplate.delete(codeKey(request.email()));
        redisTemplate.delete(attemptKey(request.email()));

        return AuthConverter.toEmailVerifyResultDTO(request.email());
    }

    private void sendEmail(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[OMO] 이메일 인증번호 안내");
        message.setText("이메일 인증번호는 " + code + " 입니다. 5분 안에 입력해 주세요.");

        try {
            mailSender.send(message);
        } catch (MailException e) {
            redisTemplate.delete(codeKey(email));
            redisTemplate.delete(attemptKey(email));
            throw new AuthException(AuthErrorCode.EMAIL_SEND_FAILED);
        }
    }

    public void validateVerifiedEmail(String email) {
        Boolean verified = redisTemplate.hasKey(verifiedKey(email));
        if (!Boolean.TRUE.equals(verified)) {
            throw new AuthException(AuthErrorCode.EMAIL_NOT_VERIFIED);
        }
    }

    public void deleteVerifiedEmail(String email) {
        redisTemplate.delete(verifiedKey(email));
    }

    private String generateCode() {
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }

    private String codeKey(String email) {
        return CODE_KEY_PREFIX + email;
    }

    private String verifiedKey(String email) {
        return VERIFIED_KEY_PREFIX + email;
    }

    private String attemptKey(String email) {
        return ATTEMPT_KEY_PREFIX + email;
    }

    private boolean isAttemptExceeded(String email) {
        String attemptValue = redisTemplate.opsForValue().get(attemptKey(email));
        return attemptValue != null && Long.parseLong(attemptValue) >= MAX_VERIFY_ATTEMPTS;
    }

    private boolean increaseAttemptAndIsExceeded(String email) {
        Long attemptCount = redisTemplate.opsForValue().increment(attemptKey(email));
        if (attemptCount != null && attemptCount == 1) {
            redisTemplate.expire(attemptKey(email), CODE_TTL);
        }
        return attemptCount != null && attemptCount >= MAX_VERIFY_ATTEMPTS;
    }
}
