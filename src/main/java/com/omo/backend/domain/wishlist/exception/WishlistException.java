package com.omo.backend.domain.wishlist.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import com.omo.backend.global.apiPayload.exception.GeneralException;

public class WishlistException extends GeneralException {

    public WishlistException(BaseErrorCode code) {
        super(code);
    }
}
