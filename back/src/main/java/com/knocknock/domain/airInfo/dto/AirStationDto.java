package com.knocknock.domain.airInfo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AirStationDto {

    private String email; // 로그인 유저의 이메일
    private String airStation; // 로그인 유저의 대기오염 측정소


}
