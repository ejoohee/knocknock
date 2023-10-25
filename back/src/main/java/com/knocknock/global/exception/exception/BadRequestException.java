package com.knocknock.global.exception.exception;

/**
 * 401(권한 없음), 404(NOT_FOUND)를 제외한 모든 에러처리
 */
public class BadRequestException extends IllegalArgumentException {

    public BadRequestException() {
        super("BAD_REQUEST_EXCEPTION 발생");
    }

    public BadRequestException(String msg) {
        super(msg);
    }

}
