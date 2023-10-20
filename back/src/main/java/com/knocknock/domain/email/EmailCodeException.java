package com.knocknock.domain.email;

import javax.mail.MessagingException;

public class EmailCodeException extends MessagingException {

    public EmailCodeException(String msg) {
        super(msg);
    }
}
