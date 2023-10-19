package com.knocknock.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SecurityExceptionMessage {

    MISMATCH_TOKEN_EMAIL("토큰 생성에 사용된 이메일과 일치하지 않습니다."),
    INVALID_TOKEN("토큰 검증 실패");

    String msg;

}
