package com.knocknock.domain.email.exception;


public class EmailCodeException extends IllegalArgumentException {

    public EmailCodeException() {
        super("EMAIL_CODE_EXCEPTION(400) 인증 코드 불일치");
    }

    public EmailCodeException(String msg) {
        super(msg);
    }
}
