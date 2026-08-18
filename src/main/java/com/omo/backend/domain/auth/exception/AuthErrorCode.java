package com.omo.backend.domain.auth.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {

    EMAIL_VERIFICATION_CODE_NOT_FOUND(HttpStatus.BAD_REQUEST, "AUTH400_1", "이메일 인증번호가 만료되었거나 존재하지 않습니다."),
    EMAIL_VERIFICATION_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "AUTH400_2", "이메일 인증번호가 일치하지 않습니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "AUTH400_3", "이메일 인증을 완료해 주세요."),
    EMAIL_VERIFICATION_ATTEMPT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "AUTH429_1", "이메일 인증번호 입력 가능 횟수를 초과했습니다. 인증번호를 다시 발급해 주세요."),
    INVALID_TOKEN_FORMAT(HttpStatus.UNAUTHORIZED, "AUTH401_1", "토큰 형식이 올바르지 않습니다. (Bearer 형식 필요)"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH401_2", "토큰이 만료되었습니다. 다시 로그인하거나 토큰을 갱신해주세요."),
    INVALID_TOKEN_SIGNATURE(HttpStatus.UNAUTHORIZED, "AUTH401_3", "토큰의 서명이 일치하지 않습니다. 변조된 토큰일 위험이 있습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH401_4", "유효하지 않은 토큰입니다. 다시 확인해주세요."),
    LOGOUT_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH401_5", "이미 로그아웃된 토큰입니다. 다시 로그인해 주세요."),
    GOOGLE_EMAIL_NOT_VERIFIED(HttpStatus.UNAUTHORIZED, "AUTH401_6", "Google에서 인증된 이메일을 확인할 수 없습니다."),
    OAUTH_STATE_INVALID(HttpStatus.BAD_REQUEST, "AUTH400_4", "OAuth 요청 상태가 만료되었거나 올바르지 않습니다."),
    OAUTH_AUTHORIZATION_FAILED(HttpStatus.BAD_REQUEST, "AUTH400_5", "Google 로그인이 취소되었거나 승인되지 않았습니다."),
    OAUTH_TICKET_INVALID(HttpStatus.BAD_REQUEST, "AUTH400_6", "OAuth 로그인 티켓이 만료되었거나 올바르지 않습니다."),
    OAUTH_SIGNUP_REQUIRED(HttpStatus.NOT_FOUND, "AUTH404_1", "해당 Google 계정으로 가입된 OMO 회원이 없습니다. Google 회원가입을 진행해 주세요."),
    SOCIAL_ACCOUNT_NOT_LINKED(HttpStatus.NOT_FOUND, "AUTH404_2", "연결된 Google 계정이 없습니다."),
    OAUTH_ACCOUNT_LINK_REQUIRED(HttpStatus.CONFLICT, "AUTH409_1", "동일한 이메일로 가입된 계정이 있습니다. 로그인 후 설정에서 Google 계정을 연동해 주세요."),
    OAUTH_ACCOUNT_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH409_2", "해당 Google 계정으로 가입된 OMO 회원이 이미 존재합니다. Google 로그인을 진행해 주세요."),
    SOCIAL_ACCOUNT_ALREADY_LINKED(HttpStatus.CONFLICT, "AUTH409_3", "이미 Google 계정이 연결되어 있습니다."),
    SOCIAL_ACCOUNT_LINKED_TO_ANOTHER_MEMBER(HttpStatus.CONFLICT, "AUTH409_4", "해당 Google 계정은 이미 다른 회원에게 연결되어 있습니다."),
    LAST_LOGIN_METHOD_UNLINK_NOT_ALLOWED(HttpStatus.CONFLICT, "AUTH409_5", "Google 계정이 유일한 로그인 수단이므로 연결을 해제할 수 없습니다."),
    GOOGLE_TOKEN_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "AUTH502_1", "Google 인증 토큰 요청에 실패했습니다."),
    GOOGLE_USER_INFO_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "AUTH502_2", "Google 사용자 정보 요청에 실패했습니다."),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH500_1", "이메일 발송에 실패했습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
