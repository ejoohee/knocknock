package com.knocknock.global.common.openapi.airInfo.dto;

import lombok.Builder;
import lombok.Data;

import javax.json.JsonObject;
import java.util.Optional;

@Data
@Builder
public class AirInfoResDto {

//    private String dataTime;
//    private String pm10Value; // 미세먼지 농도
//    private String pm25Value; // 초미세먼지 농도
//    private String o3Value; // 오존 농도
//    private String coValue; // 일산화탄소 농도
//    private String so2Value; // 아황산가스 농도
//    private String no2Value; // 이산화질소 농도
//    private String khaiValue; // 통합대기환경수치
//    private String khaiGrade; // 통합대기환경지수

    private String 측정상태; // 측정자료 상태정보(점검및교정, 장비점검, 자료이상, 통신장애)
    private String 시간;
    private String 미세먼지농도;
    private String 초미세먼지농도;
    private String 오존농도;
    private String 일산화탄소농도;
    private String 아황산가스농도;
    private String 이산화질소농도;
    private String 통합대기환경수치;
    private String 통합대기환경지수;

    public static AirInfoResDto jsonToDto(JsonObject object) {
//        // 측정 오류가 있을 시
//        if(object.get("coFlag") != null || !object.get("coFlag").equals("null")){
//            System.out.println("비정상작동");
//
//            return AirInfoResDto.builder()
//                    .측정상태(object.get("coFlag").toString())
//                    .build();
//        }
//
//
//        System.out.println("정상작동");

        // 정상작동을 하더라도 null이 나오는 경우가 있어서 Optional로 대체
        return AirInfoResDto.builder()
                .측정상태("정상측정")
                .시간(Optional.ofNullable(object.getString("dataTime")).orElse(null))
                .미세먼지농도(Optional.ofNullable(object.getString("pm10Value")).orElse(null))
                .초미세먼지농도(Optional.ofNullable(object.getString("pm25Value")).orElse(null))
                .오존농도(Optional.ofNullable(object.getString("o3Value")).orElse(null))
                .일산화탄소농도(Optional.ofNullable(object.getString("coValue")).orElse(null))
                .아황산가스농도(Optional.ofNullable(object.getString("so2Value")).orElse(null))
                .이산화질소농도(Optional.ofNullable(object.getString("no2Value")).orElse(null))
                .통합대기환경수치(Optional.ofNullable(object.getString("khaiValue")).orElse(null))
                .통합대기환경지수(Optional.ofNullable(object.getString("khaiGrade")).orElse(null))
                .build();
    }


}
