package com.knocknock.domain.user.dto.request;

import lombok.Data;

@Data
public class GoogleLoginReqDto {
    private String email;
    private String nickname;
}
