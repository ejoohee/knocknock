package com.knocknock.domain.user.exception;

import com.knocknock.global.exception.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException() {
        super("USER_NOT_FOUND_EXCEPTION 발생");
    }

    public UserNotFoundException(String msg) {
        super(msg);
    }

}
