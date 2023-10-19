package com.knocknock.domain.user.dto.password;

import lombok.Data;

@Data
public class FindPasswordReqDto {

    private String email;
    private String userName; // 먼가 하나 인증할게 필요한데 머로하지.


}
