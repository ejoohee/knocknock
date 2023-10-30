package com.knocknock.domain.user.exception;

/**
 * NOT_FOUND와 UNAUTHORIZED를 제외한 모든 UserException
 */
public class UserException extends IllegalArgumentException {

    public UserException() {
        super("USER_EXCEPTION 발생");
    }

    public UserException(String msg) {
        super(msg);
    }
}
