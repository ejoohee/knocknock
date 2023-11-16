package com.knocknock.domain.airInfo.dto;

import lombok.Builder;
import lombok.Data;

import javax.json.JsonObject;
import java.util.Date;
import java.util.Optional;

@Data
@Builder
public class AirInfoResDto {

    private String 측정상태; // 측정자료 상태정보(점검및교정, 장비점검, 자료이상, 통신장애)
    private String 시간;
    private String 통합대기환경지수;

    //    private String 미세먼지농도;
//    private String 초미세먼지농도;
//    private String 오존농도;
//    private String 일산화탄소농도;
//    private String 아황산가스농도;
//    private String 이산화질소농도;
//    private String 통합대기환경수치;

    public static AirInfoResDto jsonToDto(JsonObject object) {
//        Object pm25Flag = object.get("pm25Flag");
//        System.out.println("jsonValue : " + pm25Flag); // null
//        System.out.println(pm25Flag == null);          // false
//        System.out.println(pm25Flag.equals(null));     // false
        // null인데도 계속 false 나옴 -->>> object.isNull("pm25Flag") 로 해야 true가 나온다 !!!

        return AirInfoResDto.builder()
                .측정상태(object.isNull("pm25Flag") ? "정상측정" : object.getString("pm25Flag"))
                .시간(Optional.ofNullable(object.getString("dataTime")).orElse(null))
                .통합대기환경지수(object.isNull("khaiGrade") ? "0" : object.getString("khaiGrade")) // null 이면 0반환
//                .미세먼지농도(Optional.ofNullable(object.getString("pm10Value")).orElse(null))
//                .초미세먼지농도(Optional.ofNullable(object.getString("pm25Value")).orElse(null))
//                .오존농도(Optional.ofNullable(object.getString("o3Value")).orElse(null))
//                .일산화탄소농도(Optional.ofNullable(object.getString("coValue")).orElse(null))
//                .아황산가스농도(Optional.ofNullable(object.getString("so2Value")).orElse(null))
//                .이산화질소농도(Optional.ofNullable(object.getString("no2Value")).orElse(null))
//                .통합대기환경수치(Optional.ofNullable(object.getString("khaiValue")).orElse(null))
                .build();
    }

    public static AirInfoResDto whenIsNull() {
        return AirInfoResDto.builder()
                .측정상태("공공데이터 시스템 문제 발생")
                .시간(new Date().toString())
                .통합대기환경지수("0")
                .build();
    }

}
