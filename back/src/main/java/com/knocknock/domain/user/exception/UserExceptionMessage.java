package com.knocknock.domain.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserExceptionMessage {

    SIGN_UP_NOT_VALID("회원가입에 필요한 정보가 부족합니다."),
    EMAIL_DUPLICATED("중복 이메일입니다. 이미 존재하는 회원입니다."),
    EMAIL_CHECK_FAILED("이메일 인증을 해야합니다."),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    INFO_NOT_VALID("유저 정보가 일치하지 않습니다."),
    LOGIN_PASSWORD_ERROR("비밀번호가 일치하지 않습니다."),
    UPDATE_SAME_PASSWORD_ERROR("이전 비밀번호와 다른 비밀번호를 사용해야 합니다."),
    UPDATE_PASSWORD_ERROR("입력한 비밀번호가 일치하지 않습니다."),
    ACCESS_TOKEN_NOT_FOUND("Access token을 찾을 수 없습니다."),
    NO_ADMIN_USER("관리자 회원만 접근이 가능한 메서드입니다.");

    private String message;


}
