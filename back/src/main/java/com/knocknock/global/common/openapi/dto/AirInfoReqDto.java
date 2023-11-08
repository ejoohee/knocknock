package com.knocknock.global.common.openapi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AirInfoReqDto {

    private String stationName; // 변수가 하나인 dto는 requestBody로 사용할떄 기본생성자가 반드시 필요함
    // 안그러면 JsonParsingError가 납니다.
    

}
