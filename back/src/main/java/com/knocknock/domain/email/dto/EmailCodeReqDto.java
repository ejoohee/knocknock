package com.knocknock.domain.email.dto;

import lombok.Data;

@Data
public class EmailCodeReqDto {

    private String email;
    private String code;
}
