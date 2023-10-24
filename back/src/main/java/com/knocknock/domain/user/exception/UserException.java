package com.knocknock.domain.user.exception;

import com.knocknock.global.exception.exception.NotFoundException;

public class UserException extends NotFoundException {

    public UserException(String msg) {
        super(msg);
    }
}
