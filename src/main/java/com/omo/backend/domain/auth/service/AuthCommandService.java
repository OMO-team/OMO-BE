package com.omo.backend.domain.auth.service;

import com.omo.backend.domain.auth.converter.AuthConverter;
import com.omo.backend.domain.auth.dto.AuthRequestDTO;
import com.omo.backend.domain.auth.dto.AuthResponseDTO;
import com.omo.backend.domain.auth.exception.AuthErrorCode;
import com.omo.backend.domain.auth.exception.AuthException;
import com.omo.backend.domain.member.entity.Member;
import com.omo.backend.domain.member.exception.MemberErrorCode;
import com.omo.backend.domain.member.exception.MemberException;
import com.omo.backend.domain.member.repository.MemberRepository;
import com.omo.backend.global.security.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthCommandService {

    private static final String REFRESH_TOKEN_KEY_PREFIX = "RT:";
    private static final String BLACKLIST_KEY_PREFIX = "BL:";

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    public AuthResponseDTO.LoginResultDTO login(AuthRequestDTO.LoginDTO request) {
        // 이메일로 회원 조회
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_LOGIN_ID_OR_PASSWORD));

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new MemberException(MemberErrorCode.INVALID_LOGIN_ID_OR_PASSWORD);
        }

        // 이메일과 비밀번호가 일치할 경우 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(member.getId(), member.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId(), member.getEmail());

        saveRefreshToken(member.getId(), refreshToken);

        return AuthConverter.toLoginResultDTO(member.getId(), accessToken, refreshToken);
    }

    public AuthResponseDTO.ReissueResultDTO reissue(AuthRequestDTO.ReissueDTO request) {
        String refreshToken = request.refreshToken();

        try {
            jwtTokenProvider.isValid(refreshToken);

            if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
                throw new AuthException(AuthErrorCode.TOKEN_INVALID);
            }

            // 토큰에서 회원 정보 추출
            Long memberId = jwtTokenProvider.getMemberId(refreshToken);

            String refreshTokenKey = refreshTokenKey(memberId);
            String savedRefreshToken = redisTemplate.opsForValue().get(refreshTokenKey);

            // 토큰 일치 여부 및 재사용 검사
            if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
                redisTemplate.delete(refreshTokenKey);
                throw new AuthException(AuthErrorCode.TOKEN_INVALID);
            }

            // 새 토큰 발급을 위한 회원 조회
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new AuthException(AuthErrorCode.TOKEN_INVALID));

            // 유효한 refreshToken일 경우 토큰 재발급
            String newAccessToken = jwtTokenProvider.createAccessToken(member.getId(), member.getEmail());
            String newRefreshToken = jwtTokenProvider.createRefreshToken(member.getId(), member.getEmail());

            saveRefreshToken(memberId, newRefreshToken);

            return AuthConverter.toReissueResultDTO(newAccessToken, newRefreshToken);
        } catch (ExpiredJwtException e) {
            throw new AuthException(AuthErrorCode.TOKEN_EXPIRED);
        } catch (SignatureException | MalformedJwtException e) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN_SIGNATURE);
        } catch (AuthException e) {
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }
    }

    public void logout(Long memberId, String accessToken) {
        // Redis에 저장된 RefreshToken 삭제
        redisTemplate.delete(refreshTokenKey(memberId));

        // 로그아웃 이후 accessToken을 통한 재로그인 요청 방지
        long expiration = jwtTokenProvider.getExpiration(accessToken);
        if (expiration <= 0) {
            throw new AuthException(AuthErrorCode.TOKEN_EXPIRED);
        }

        redisTemplate.opsForValue().set(
                blacklistKey(accessToken),
                "logout",
                Duration.ofMillis(expiration)
        );
    }

    private void saveRefreshToken(Long memberId, String refreshToken) {
        long expiration = jwtTokenProvider.getExpiration(refreshToken);
        if (expiration <= 0) {
            throw new AuthException(AuthErrorCode.TOKEN_EXPIRED);
        }

        redisTemplate.opsForValue().set(
                refreshTokenKey(memberId),
                refreshToken,
                Duration.ofMillis(expiration)
        );
    }

    private String refreshTokenKey(Long memberId) {
        return REFRESH_TOKEN_KEY_PREFIX + memberId;
    }

    private String blacklistKey(String accessToken) {
        return BLACKLIST_KEY_PREFIX + accessToken;
    }
}
