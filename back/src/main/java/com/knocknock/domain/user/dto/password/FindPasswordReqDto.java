package com.knocknock.domain.user.dto.password;

import lombok.Data;

@Data
public class FindPasswordReqDto {

    private String nickname; // 먼가 하나 인증할게 필요해서 닉네임
    private String email;

}
