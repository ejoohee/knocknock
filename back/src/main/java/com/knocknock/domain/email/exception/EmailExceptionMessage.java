package com.knocknock.domain.email.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailExceptionMessage {

    EMAIL_NOT_SENT("이메일 발송을 실패하였습니다."),
    EMAIL_CODE_NOT_FOUND("해당 이메일로 유효한 인증 코드가 존재하지 않습니다."),
    EMAIL_CODE_NOT_VALID("이메일 인증 코드가 일치하지 않습니다.");

    private String message;

}
