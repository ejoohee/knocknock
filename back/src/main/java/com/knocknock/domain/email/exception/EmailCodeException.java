package com.knocknock.domain.email.exception;


import com.knocknock.global.exception.exception.MessagingException;

public class EmailCodeException extends MessagingException {

    public EmailCodeException(String msg) {
        super(msg);
    }
}
