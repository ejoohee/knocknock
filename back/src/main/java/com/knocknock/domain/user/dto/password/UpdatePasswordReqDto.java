package com.knocknock.domain.user.dto.password;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UpdatePasswordReqDto {

    private String password;

    @Length(min = 8, max = 16, message = "비밀번호는 8 ~ 16자리로 입력해주세요.") // 이거 적용이안대넹
    private String newPassword;

    @Length(min = 8, max = 16, message = "새로운 비밀번호와 동일하게 입력해주세요.")
    private String newPasswordCheck;

}
