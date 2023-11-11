package com.knocknock.global.common.openapi.airInfo;

import com.knocknock.domain.user.dao.UserRepository;
import com.knocknock.global.common.openapi.airInfo.dto.AirInfoReqDto;
import com.knocknock.global.common.openapi.airInfo.dto.AirInfoResDto;
import com.knocknock.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirInfoService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private StringBuilder urlBuilder, sb;
    private BufferedReader br;


    private final String API_KEY = "XymYPoqUHNl0U%2Fuo0Tbs6LJ5VZQfWjXVfWjMBAfEnBFI8fSenYRRca0X%2B%2FRrmACkJYcS4WlJvNyf1NA4adMJvA%3D%3D";
    private final String GET_STATION_URL = "http://apis.data.go.kr/B552584/MsrstnInfoInqireSvc";

    public String pickStationByAddress(String address) throws IOException {
        urlBuilder = new StringBuilder("http://openapi.epost.go.kr/postal/retrieveNewAdressAreaCdService/retrieveNewAdressAreaCdService/getNewAddressListAreaCd"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + API_KEY); /*Service Key*/
//        urlBuilder.append("&" + URLEncoder.encode("searchSe","UTF-8") + "=" + URLEncoder.encode("dong", "UTF-8")); /*dong : 동(읍/면)명road :도로명[default]post : 우편번호*/
        urlBuilder.append("&" + URLEncoder.encode("srchwrd","UTF-8") + "=" + URLEncoder.encode(address, "UTF-8")); /*검색어*/
        urlBuilder.append("&" + URLEncoder.encode("countPerPage","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지당 출력될 개수를 지정*/
        urlBuilder.append("&" + URLEncoder.encode("currentPage","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*출력될 페이지 번호*/

        URL url = new URL(urlBuilder.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

//        System.out.println("Response code: " + conn.getResponseCode());

//        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300)
//            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        else
//            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//
//        StringBuilder sb = new StringBuilder();
//
//        String line;
//        while ((line = br.readLine()) != null) {
//            sb.append(line);
//        }

//        br.close();


        conn.disconnect();

//        System.out.println(sb.toString());




        return null;
    }



    public AirInfoResDto getAirInfoByRegion(AirInfoReqDto reqDto) throws IOException {
        // 1. URL을 만들기 위한 StringBuilder
        urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty"); /*URL*/

        // 2. OpenAPI의 요청 규격에 맞는 파라미터 생성, 발급받은 인증키.
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + API_KEY); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("returnType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*xml 또는 json*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*한 페이지 결과 수*/ // 가장 실시간 정보만 받아오도록 1설정
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("stationName", "UTF-8") + "=" + URLEncoder.encode(reqDto.getStationName(), "UTF-8")); /*측정소 이름*/
        urlBuilder.append("&" + URLEncoder.encode("dataTerm", "UTF-8") + "=" + URLEncoder.encode("DAILY", "UTF-8")); /*요청 데이터기간(1일: DAILY, 1개월: MONTH, 3개월: 3MONTH)*/
        urlBuilder.append("&" + URLEncoder.encode("ver", "UTF-8") + "=" + URLEncoder.encode("1.3", "UTF-8")); /*버전별 상세 결과 참고*/

        // 3.URL 객체 생성
        URL url = new URL(urlBuilder.toString());

        // 4. 요청하고자 하는 URL과 통신하기 위한 Connection 객체 생성
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // 5. 통신을 위한 메소드, Content-type 세팅
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-type", "application/json");

        // 6. 전달받은 데이터를 BufferedReader 객체로 저장
        if(connection.getResponseCode() >= 200 && connection.getResponseCode() <= 300)  // OK
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        else
            br = new BufferedReader(new InputStreamReader(connection.getErrorStream())); // ERROR

        // 7. 저장된 데이터를 한줄씩 읽어 StringBuilder 객체로 저장
        sb = new StringBuilder();
        String line;
        while((line = br.readLine()) != null) {
            sb.append(line);
        }

        // 8. 객체 해제
        br.close();
        connection.disconnect();

        StringReader stringReader = new StringReader(sb.toString());
        JsonReader jsonReader = null;
        JsonObject jsonObject = null;

        boolean success = true;
        while(success) {
            try {
                jsonReader = Json.createReader(stringReader);
                jsonObject = jsonReader.readObject();

                success = false;
            } catch (JsonParsingException e) {
                log.error("JSON 파싱 실패. 재시도 GO");

                try {
                    Thread.sleep(1000);

                    getAirInfoByRegion(reqDto);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }

                log.info("JSON  파싱 성공");
            }
        }

        jsonObject = jsonObject.getJsonObject("response").getJsonObject("body");
        JsonArray items = jsonObject.getJsonArray("items");
        log.info("items(ARRAY) : {}", items.get(0));

        jsonObject = items.getJsonObject(0);
        log.info("jsonObejct : {}", jsonObject);
//        log.info("하나뽑아와 : {}", jsonObject.get("mangName"));
        log.info(" 스트링만 : {}", jsonObject.getString("mangName")); // 이렇게 뽑아야 따옴표없이 스트링만 뽑아옴

        return AirInfoResDto.jsonToDto(jsonObject);

        // ConnectionException뜨면 다시 보낼수 있게 추가하기 !!!!!!!!!!!


        // 9. 전달받은 데이터 확인!
        // JSON 파싱을 위한 JsonParser 선언
//        JSONParser jsonParser = new JSONParser();

        // JSON 데이터를 넣어 JSON Object로 만들기
//        JSONObject jsonObject = (JSONObject) jsonParser.parse(sb.toString());
//        JSONObject response = (JSONObject) jsonObject.get("response"); // JSONOBject로 형변환 해주지않으면 그냥 STring(Object)가 되어서 키로 값을 못뽑아옴
//        JSONObject body = (JSONObject) response.get("body");
//        // 배열 추출
//        JSONArray items = (JSONArray) body.get("items");
//        JSONObject item = (JSONObject)items.get(0);
//
//        log.info("item ; {}", item);
//
//        return AirInfoResDto.jsonToDto(item);

    }


}
