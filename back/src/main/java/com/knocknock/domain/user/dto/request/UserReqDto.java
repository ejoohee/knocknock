package com.knocknock.domain.user.dto.request;

import com.knocknock.domain.user.domain.UserType;
import com.knocknock.domain.user.domain.Users;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;

@Data
public class UserReqDto {

    @Email
    private String email;

    @Length(min = 8, max = 16, message = "비밀번호는 8~16자리로 입력해주세요.")
    private String password;

    @Length(min = 2, max = 5, message = "닉네임은 2~5글자로 입력해주세요.")
    private String nickname;

    private String address;

    // 소셜인지 어케 체크하징
    public Users dtoToEntity() {
        return Users.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .userType(UserType.ROLE_USER.getValue())
                .address(address)
                .build();
    }

}
