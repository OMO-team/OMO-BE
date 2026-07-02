package com.omo.backend.global.apiPayload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import com.omo.backend.global.apiPayload.code.GeneralSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result", "timestamp"})
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;

    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T result;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime timestamp;

    // 요청 성공 - 200 OK
    public static <T> ApiResponse<T> onSuccess(T result) {
        return new ApiResponse<>(true, GeneralSuccessCode.OK.getCode(), GeneralSuccessCode.OK.getMessage(), result, LocalDateTime.now());
    }

    // 리소스 생성 - 201 CREATED
    public static <T> ApiResponse<T> created(T result) {
        return new ApiResponse<>(true, GeneralSuccessCode.CREATED.getCode(), GeneralSuccessCode.CREATED.getMessage(), result, LocalDateTime.now());
    }

    // 요청 접수 - 202 ACCEPTED
    public static <T> ApiResponse<T> accepted(T result) {
        return new ApiResponse<>(true, GeneralSuccessCode.ACCEPTED.getCode(), GeneralSuccessCode.ACCEPTED.getMessage(), result, LocalDateTime.now());
    }

    // 요청 실패 - 기본 에러 메시지 사용
    public static <T> ApiResponse<T> onFailure(BaseErrorCode baseErrorCode, T result) {
        return new ApiResponse<>(false, baseErrorCode.getCode(), baseErrorCode.getMessage(), result, LocalDateTime.now());
    }

    // 요청 실패 - 커스텀 에러 메시지 사용
    public static <T> ApiResponse<T> onFailure(BaseErrorCode baseErrorCode, String customMessage, T result) {
        return new ApiResponse<>(false, baseErrorCode.getCode(), customMessage, result, LocalDateTime.now());
    }
}
