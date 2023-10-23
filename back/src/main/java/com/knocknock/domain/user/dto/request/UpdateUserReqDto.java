package com.knocknock.domain.user.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserReqDto {

    private String nickname;
    private String address;
    private Integer giroCode;

}
