package com.knocknock.domain.user.exception;

import com.knocknock.global.exception.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(String msg) {
        super(msg);
    }
}
