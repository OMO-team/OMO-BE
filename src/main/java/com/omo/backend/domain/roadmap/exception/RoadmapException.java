package com.omo.backend.domain.roadmap.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import com.omo.backend.global.apiPayload.exception.GeneralException;

public class RoadmapException extends GeneralException {
    public RoadmapException(BaseErrorCode code) {
        super(code);
    }
}
