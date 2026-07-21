package com.omo.backend.domain.roadmap.exception;

import com.omo.backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RoadmapErrorCode implements BaseErrorCode {

    INVALID_DEPARTURE_DATE(
            HttpStatus.BAD_REQUEST,
            "ROADMAP400_1",
            "유효하지 않은 출국일입니다."
    ),
    UNSUPPORTED_CITY_PURPOSE(
            HttpStatus.BAD_REQUEST,
            "ROADMAP400_2",
            "해당 도시에서 지원하지 않는 목적입니다."
    ),
    EMPTY_ROADMAP_TEMPLATE(
            HttpStatus.UNPROCESSABLE_CONTENT,
            "ROADMAP422_1",
            "로드맵 템플릿에 태스크가 없습니다."
    ),
    INVALID_TEMPLATE_DEPENDENCY(
            HttpStatus.UNPROCESSABLE_CONTENT,
            "ROADMAP422_2",
            "로드맵 템플릿의 태스크 의존성이 올바르지 않습니다."
    ),
    SELF_REFERENCING_DEPENDENCY(
            HttpStatus.UNPROCESSABLE_CONTENT,
            "ROADMAP422_3",
            "태스크 템플릿은 자기 자신을 선행 태스크로 가질 수 없습니다."
    ),
    DUPLICATE_TEMPLATE_DEPENDENCY(
            HttpStatus.UNPROCESSABLE_CONTENT,
            "ROADMAP422_4",
            "중복된 태스크 템플릿 의존성이 존재합니다."
    ),
    CYCLIC_TEMPLATE_DEPENDENCY(
            HttpStatus.UNPROCESSABLE_CONTENT,
            "ROADMAP422_5",
            "태스크 템플릿 의존성에 순환이 존재합니다."
    ),
    DUPLICATE_TEMPLATE_DOCUMENT(
            HttpStatus.UNPROCESSABLE_CONTENT,
            "ROADMAP422_6",
            "중복된 태스크 템플릿 서류가 존재합니다."
    ),
    DUPLICATE_TASK_DEPENDENCY(
            HttpStatus.UNPROCESSABLE_CONTENT,
            "ROADMAP422_7",
            "중복된 태스크 의존성을 생성할 수 없습니다."
    ),
    DUPLICATE_TASK_DOCUMENT(
            HttpStatus.UNPROCESSABLE_CONTENT,
            "ROADMAP422_8",
            "동일한 태스크에 같은 서류를 중복 생성할 수 없습니다."
    ),
    ROADMAP_TEMPLATE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "ROADMAP404_1",
            "도시와 목적에 대응하는 로드맵 템플릿이 없습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
