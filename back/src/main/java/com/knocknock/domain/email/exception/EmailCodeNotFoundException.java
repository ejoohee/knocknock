package com.knocknock.domain.email.exception;

import com.knocknock.global.exception.exception.NotFoundException;

public class EmailCodeNotFoundException extends NotFoundException {

    public EmailCodeNotFoundException() {
        super("EMAIL_CODE_NOT_FOUND_EXCEPTION 인증코드가 존재하지 않음");
    }

    public EmailCodeNotFoundException(String msg) {
        super(msg);
    }
}
