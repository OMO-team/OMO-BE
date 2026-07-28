package com.omo.backend.global.storage.exception;

import com.omo.backend.global.apiPayload.exception.GeneralException;

public class StorageException extends GeneralException {

    public StorageException(StorageErrorCode code) {
        super(code);
    }
}
