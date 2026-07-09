package com.omo.backend.domain.auth.service;

import com.omo.backend.domain.auth.converter.AuthConverter;
import com.omo.backend.domain.auth.dto.AuthRequestDTO;
import com.omo.backend.domain.auth.dto.AuthResponseDTO;
import com.omo.backend.domain.member.entity.Member;
import com.omo.backend.domain.member.exception.MemberErrorCode;
import com.omo.backend.domain.member.exception.MemberException;
import com.omo.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthCommandService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponseDTO.LoginResultDTO login(AuthRequestDTO.LoginDTO request) {
        // 이메일로 회원 조회
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_LOGIN_ID_OR_PASSWORD));

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new MemberException(MemberErrorCode.INVALID_LOGIN_ID_OR_PASSWORD);
        }

        // 토큰 생성
        String accessToken = "";
        String refreshToken = "";

        return AuthConverter.toLoginResultDTO(member.getId(), accessToken, refreshToken);
    }
}
