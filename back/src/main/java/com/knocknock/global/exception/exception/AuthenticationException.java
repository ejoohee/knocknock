package com.knocknock.global.exception.exception;

/**
 * 토큰 인증은 됐으나,
 * 로그인 사용자가 해당 메서드 사용 권한이 없음
 */
public class AuthenticationException extends IllegalArgumentException {

    public AuthenticationException() {
        super("AUTHENTICATION_EXCEPTION 발생");
    }

    public AuthenticationException(String msg) {
        super(msg);
    }
}
