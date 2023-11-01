package com.knocknock.domain.model.exception;

import com.knocknock.global.exception.exception.NotFoundException;

public class ModelNotFoundException extends NotFoundException {

    public ModelNotFoundException(String msg) {
        super(msg);
    }

}
