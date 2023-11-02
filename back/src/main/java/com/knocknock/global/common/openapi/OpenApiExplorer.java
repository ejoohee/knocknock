package com.knocknock.global.common.openapi;

import com.knocknock.global.common.openapi.dto.AirInfoReqDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class OpenApiExplorer {

    static final Charset UTF_8 = StandardCharsets.UTF_8;

    public void getAirInfoByRegion(AirInfoReqDto reqDto) throws IOException {
        // 1. URL을 만들기 위한 StringBuilder
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty");

        // 2. OpenAPI의 요청 규격에 맞는 파라미터 생성, 발급받은 인증키.
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", UTF_8) + "=" + URLEncoder.encode("XymYPoqUHNl0U%2Fuo0Tbs6LJ5VZQfWjXVfWjMBAfEnBFI8fSenYRRca0X%2B%2FRrmACkJYcS4WlJvNyf1NA4adMJvA%3D%3D", UTF_8));
        urlBuilder.append("&" + URLEncoder.encode("returnType", UTF_8) + "=" + URLEncoder.encode("JSON", UTF_8));
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", UTF_8) + "=" + URLEncoder.encode("100", UTF_8));
        urlBuilder.append("&" + URLEncoder.encode("pageNo", UTF_8) + "=" + URLEncoder.encode("1", UTF_8));
        urlBuilder.append("&" + URLEncoder.encode("stationName", UTF_8) + "=" + URLEncoder.encode(reqDto.getStationName(), UTF_8));
        urlBuilder.append("&" + URLEncoder.encode("dateTerm", UTF_8) + "=" + URLEncoder.encode("DAILY", UTF_8));
        urlBuilder.append("&" + URLEncoder.encode("ver", UTF_8) + "=" + URLEncoder.encode("1.3", UTF_8));

        // 3.URL 객체 생성
        URL url = new URL(urlBuilder.toString());

        // 4. 요청하고자 하는 URL과 통신하기 위한 Connection 객체 생성
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // 5. 통신을 위한 메소드, Content-type 세팅
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-type", "application/json");

        // 6. 통신 응답 코드 체크
        System.out.println("Response Code : " + connection.getResponseCode());

        // 7. 전달받은 데이터를 BufferedReader 객체로 저장
        BufferedReader br;
        if(connection.getResponseCode() >= 200 && connection.getResponseCode() <= 300)  // OK
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        else
            br = new BufferedReader(new InputStreamReader(connection.getErrorStream())); // ERROR

        // 8. 저장된 데이터를 한줄씩 읽어 StringBuilder 객체로 저장
        StringBuilder sb = new StringBuilder();
        String line;
        while((line = br.readLine()) != null) {
            sb.append(line);
        }

        // 9. 객체 해제
        br.close();
        connection.disconnect();

        // 10. 전달받은 데이터 확인!
        System.out.println(sb.toString());

    }



}
