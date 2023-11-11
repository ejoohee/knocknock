package com.knocknock.global.common.openapi.airInfo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AirInfoReqDto {
    
    // 변수가 하나인 DTO는 requestBody로 사용시 기본생성자가 반드시 필요
    // 안그러면 JsonParsingError 발생
    private String stationName;
    
}
