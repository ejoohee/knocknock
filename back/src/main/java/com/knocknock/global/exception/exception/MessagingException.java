package com.knocknock.global.exception.exception;

public class MessagingException extends IllegalArgumentException {

    public MessagingException() {
        super("MESSAGING_EXCEPTION 발생");
    }

    public MessagingException(String msg) {
        super(msg);
    }
}
