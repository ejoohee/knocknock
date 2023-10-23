package com.knocknock.domain.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResDto {

    private String nickname;
    private String email;
    private Integer giroCode;
    private String address;

    // dtoTOEntity

}
