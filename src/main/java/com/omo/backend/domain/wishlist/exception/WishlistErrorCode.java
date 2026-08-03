package com.omo.backend.domain.wishlist.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum WishlistErrorCode implements BaseErrorCode {
    UNSUPPORTED_CITY_PURPOSE(
            HttpStatus.BAD_REQUEST,
            "WISHLIST400_1",
            "해당 도시에서 지원하지 않는 목적입니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
