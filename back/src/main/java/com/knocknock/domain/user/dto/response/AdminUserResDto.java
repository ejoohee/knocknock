package com.knocknock.domain.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUserResDto {

    private Long userId;
    private String nickname;
    private String email;
    private Integer giroCode;
    private String address;

}
