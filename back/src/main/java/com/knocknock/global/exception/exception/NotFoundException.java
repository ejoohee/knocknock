package com.knocknock.global.exception.exception;

public class NotFoundException extends IllegalArgumentException {

    public NotFoundException() {
        super("NOT_FOUND_EXCEPTION 발생");
    }

    public NotFoundException(String msg) {
        super(msg);
    }
}
