package com.knocknock.domain.airInfo.dto;

import lombok.Builder;
import lombok.Data;

import javax.json.JsonObject;

@Data
@Builder
public class TmPointDto {

    private String tmX;
    private String tmY;

    public static TmPointDto jsonToDto(JsonObject object) {
        return TmPointDto.builder()
                .tmX(object.getString("tmX"))
                .tmY(object.getString("tmY"))
                .build();
    }


}
