package com.wanted.teamV.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    NOT_FOUND(HttpStatus.BAD_REQUEST, "요청사항을 찾지 못했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 발생했습니다."),
    INVALID_PAGE_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 페이지 요청입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "잘못된 비밀번호입니다."),
    DUPLICATE_ACCOUNT(HttpStatus.BAD_REQUEST, "중복된 계정입니다."),
    EXPIRE_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 토큰입니다."),
    EMPTY_AUTHORIZATION_HEADER(HttpStatus.BAD_REQUEST, "인증헤더가 비어있습니다."),
    FAIL_READ_FILE(HttpStatus.INTERNAL_SERVER_ERROR, "파일 읽기 작업이 실패했습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    NULL_LAT_VALUE(HttpStatus.BAD_REQUEST, "위도 값이 비어있습니다."),
    NULL_LON_VALUE(HttpStatus.BAD_REQUEST, "경도 값이 비어있습니다."),
    INVALID_LAT_RANGE(HttpStatus.BAD_REQUEST, "위도 범위를 넘었습니다. (33 ~ 43)"),
    INVALID_LON_RANGE(HttpStatus.BAD_REQUEST, "경도 범위를 넘었습니다. (124 ~ 132)"),
    INVALID_RESTAURANT_SORT_TYPE(HttpStatus.BAD_REQUEST, "정렬 값이 잘못되었습니다."),
    RESTAURANT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 맛집을 찾을 수 없습니다."),
    INVALID_RATING_ENTITY(HttpStatus.BAD_REQUEST, "유효하지 않은 평가 객체입니다."),
    ;

    private final HttpStatus status;
    private final String message;

    ErrorCode(final HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}