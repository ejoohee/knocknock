package com.knocknock.domain.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReissueTokenResDto {

    private String accessToken;
    private String refreshToken;

}
