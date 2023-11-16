package com.knocknock.domain.airInfo.service;

import com.knocknock.domain.user.dao.UserRepository;
import com.knocknock.domain.user.domain.Users;
import com.knocknock.domain.user.exception.UserExceptionMessage;
import com.knocknock.domain.user.exception.UserNotFoundException;
import com.knocknock.domain.airInfo.dto.AirInfoResDto;
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

    private JsonObject jsonObject;
    private JsonReader jsonReader;
    private StringReader stringReader;

    private final String API_KEY = "XymYPoqUHNl0U%2Fuo0Tbs6LJ5VZQfWjXVfWjMBAfEnBFI8fSenYRRca0X%2B%2FRrmACkJYcS4WlJvNyf1NA4adMJvA%3D%3D";


    public AirInfoResDto getAirInfoByRegion(String token, int timeOut) throws IOException {
        log.info("[실시간 대기정보 조회] 메서드에 진입했어요. ----------------------------------------------------");

        // 로그인 사용자의 측정소 뽑아오기
        String email = jwtUtil.getLoginEmail(token);
        log.info("[실시간 대기정보 조회] email : {}", email);

        Users loginUser = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[실시간 대기정보 조회] 로그인 후 사용 가능");
                    return new UserNotFoundException(UserExceptionMessage.USER_NOT_FOUND.getMessage());
                });

        String stationName = loginUser.getAirStation();
        log.info("[실시간 대기정보 조회] {} 측정소 대기 정보 조회", stationName);

        if(stationName == null) {
            log.error("[대기정보 조회] 측정소가 등록되어 있지 않습니다. 측정소를 임의로 강남구로 설정합니다.");
            stationName = "강남구";
        }

        // 1. URL을 만들기 위한 StringBuilder
        urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty"); /*URL*/

        // 2. OpenAPI의 요청 규격에 맞는 파라미터 생성, 발급받은 인증키.
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + API_KEY); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("returnType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*xml 또는 json*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*한 페이지 결과 수*/ // 가장 실시간 정보만 받아오도록 1설정
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("stationName", "UTF-8") + "=" + URLEncoder.encode(stationName, "UTF-8")); /*측정소 이름*/
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

        stringReader = new StringReader(sb.toString());
        jsonReader = null;
        jsonObject = null;

        while(timeOut > 0) {
            try {
                jsonReader = Json.createReader(stringReader);
                jsonObject = jsonReader.readObject();

                timeOut = 0;
            } catch (JsonParsingException e) {
                log.error("JSON 파싱 실패. 재시도 GO");
                log.error("5번 기회 중 {}번 기회 남음.", timeOut-1);

                try {
                    Thread.sleep(3000);

                    return getAirInfoByRegion(token, timeOut-1);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        if(jsonObject.getJsonObject("response") == null) {
            log.error("공공데이터를 불러오지 못했어여ㅠ");
            return AirInfoResDto.whenIsNull();
        }

        jsonObject = jsonObject.getJsonObject("response").getJsonObject("body");
        JsonArray items = jsonObject.getJsonArray("items");
        log.info("items(ARRAY) : {}", items.get(0));

        jsonObject = items.getJsonObject(0);
        log.info("jsonObejct : {}", jsonObject);

        return AirInfoResDto.jsonToDto(jsonObject);

        // ConnectionException뜨면 다시 보낼수 있게 추가하기 !!!!!!!!!!!

        // 9. 전달받은 데이터 확인!

    }

}
