package com.knocknock.domain.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResDto {

    private String accessToken;
    private String refreshToken;
    private String nickname;
    private String email;
    private String address;

}
