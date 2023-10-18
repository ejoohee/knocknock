package com.knocknock.domain.user.dto;

import lombok.Data;

@Data
public class CheckEmailCodeReqDto {

    private String email;
    private String code;
}
