package com.knocknock.domain.user.exception;

import com.knocknock.global.exception.exception.AuthenticationException;
import com.knocknock.global.exception.exception.TokenException;

public class UserUnAuthorizedException extends AuthenticationException {

    public UserUnAuthorizedException() {
        super("USER_UNAUTHORIZED_EXCEPTION 발생");
    }

    public UserUnAuthorizedException(String msg) {
        super(msg);
    }


}
