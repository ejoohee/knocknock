package com.knocknock.domain.user.exception;

import com.knocknock.global.exception.exception.AuthenticationException;

/**
 * 관리자 계정이 아닌데 관리자 전용 메서드에 접근하려고 할때 
 * 권한 없음 에러
 */
public class UserUnAuthorizedException extends AuthenticationException {

    public UserUnAuthorizedException() {
        super("USER_UNAUTHORIZED_EXCEPTION 발생");
    }

    public UserUnAuthorizedException(String msg) {
        super(msg);
    }


}
