package com.knocknock.domain.user.dto.password;

import lombok.Data;

@Data
public class UpdatePasswordReqDto {

    private String password;
    private String newPassword;
    private String newPasswordCheck;

}
