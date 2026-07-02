package com.omo.backend.global.apiPayload.handler;

import com.omo.backend.global.apiPayload.ApiResponse;
import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import com.omo.backend.global.apiPayload.code.GeneralErrorCode;
import com.omo.backend.global.apiPayload.exception.GeneralException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class GeneralExceptionAdvice {

    /**
     * 프로젝트 공통 커스텀 예외 처리
     */
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(GeneralException e) {
        BaseErrorCode code = e.getBaseErrorCode();

        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.onFailure(code, null));
    }

    /**
     * @Valid 검증 실패 (RequestBody)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(error.getField(), error.getDefaultMessage());
        }

        BaseErrorCode code = GeneralErrorCode.VALID_FAIL;
        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.onFailure(code, errors));
    }

    /**
     * @Validated 검증 실패 (RequestParam, PathVariable 등)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolationException(
            ConstraintViolationException e
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        e.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String fieldName = propertyPath.contains(".")
                    ? propertyPath.substring(propertyPath.lastIndexOf('.') + 1)
                    : propertyPath;

            errors.putIfAbsent(fieldName, violation.getMessage());
        });

        BaseErrorCode code = GeneralErrorCode.VALID_FAIL;
        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.onFailure(code, errors));
    }

    /**
     * JSON 형식 오류, 잘못된 RequestBody 등
     * 예: enum 잘못 들어옴, 숫자 자리에 문자열 들어옴, JSON 문법 오류
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e
    ) {
        BaseErrorCode code = GeneralErrorCode.BAD_REQUEST;

        // 인자 3개짜리 onFailure 사용: (코드, 커스텀 메시지, 데이터(null))
        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.onFailure(code, "요청 본문 형식이 올바르지 않습니다.", null));
    }

    /**
     * 필수 RequestParam 누락
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e
    ) {
        BaseErrorCode code = GeneralErrorCode.BAD_REQUEST;
        String message = String.format("필수 요청 파라미터 '%s'가 누락되었습니다.", e.getParameterName());

        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.onFailure(code, message, null));
    }

    /**
     * 필수 PathVariable 누락
     */
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingPathVariableException(
            MissingPathVariableException e
    ) {
        BaseErrorCode code = GeneralErrorCode.BAD_REQUEST;
        String message = String.format("필수 경로 변수 '%s'가 누락되었습니다.", e.getVariableName());

        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.onFailure(code, message, null));
    }

    /**
     * RequestParam / PathVariable 타입 불일치
     * 예: Long 자리에 문자열 전달
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e
    ) {
        BaseErrorCode code = GeneralErrorCode.BAD_REQUEST;
        String message = String.format("요청 값 '%s'의 타입이 올바르지 않습니다.", e.getName());

        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.onFailure(code, message, null));
    }

    /**
     * 잘못된 요청 파라미터 / 비즈니스 로직 내 잘못된 인자
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException e
    ) {
        BaseErrorCode code = GeneralErrorCode.BAD_REQUEST;

        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.onFailure(code, e.getMessage(), null));
    }

    /**
     * 지원하지 않는 HTTP Method
     * 예: POST만 허용인데 GET 요청
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e
    ) {
        BaseErrorCode code = GeneralErrorCode.METHOD_NOT_ALLOWED;

        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.onFailure(code, "지원하지 않는 HTTP 메서드입니다.", null));
    }

    /**
     * 그 외 정의되지 않은 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnhandledException(Exception e) {
        log.error("Unhandled Exception: ", e);

        BaseErrorCode code = GeneralErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.onFailure(code, "서버 내부 오류가 발생했습니다.", null));
    }
}
