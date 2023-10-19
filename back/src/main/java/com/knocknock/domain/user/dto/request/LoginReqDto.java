package com.knocknock.domain.user.dto.request;

import lombok.Data;

@Data
public class LoginReqDto {

    private String email;
    private String password;

}
