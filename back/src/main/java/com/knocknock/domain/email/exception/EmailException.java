package com.knocknock.domain.email.exception;

public class EmailException extends IllegalArgumentException {

    public EmailException() {
        super("EMAIL_EXCEPTION 이메일 발신 실패");
    }

    public EmailException(String msg) {
        super(msg);
    }
}
