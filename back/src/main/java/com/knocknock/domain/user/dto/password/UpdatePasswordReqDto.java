package com.knocknock.domain.user.dto.password;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UpdatePasswordReqDto {

    private String password;

    @Length(min = 8, max = 16, message = "비밀번호는 8 ~ 16자리로 입력해주세요.")
    private String newPassword;
    private String newPasswordCheck;

}
