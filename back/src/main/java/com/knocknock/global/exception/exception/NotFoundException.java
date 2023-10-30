package com.knocknock.global.exception.exception;


/**
 * NOT_FOUND(404)
 */
public class NotFoundException extends IllegalArgumentException {

    public NotFoundException() {
        super("NOT_FOUND_EXCEPTION 발생");
    }

    public NotFoundException(String msg) {
        super(msg);
    }
}
