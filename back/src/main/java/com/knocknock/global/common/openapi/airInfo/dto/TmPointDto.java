package com.knocknock.global.common.openapi.airInfo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TmPointDto {

    private String tmX;
    private String tmY;

//    public static TmPointDto jsonToDto(JSONObject object) {
//        return TmPointDto.builder()
//                .tmX(object.get("tmX").toString())
//                .tmY(object.get("tmY").toString())
//                .build();
//    }


}
