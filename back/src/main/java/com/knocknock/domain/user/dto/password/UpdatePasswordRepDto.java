package com.knocknock.domain.user.dto.password;

import lombok.Data;

@Data
public class UpdatePasswordRepDto {

    private String password;
    private String newPassword;
    private String newPasswordCheck;

}
