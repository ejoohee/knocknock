package com.knocknock.domain.waste.service;

import com.knocknock.domain.waste.dao.WasteRepository;
import com.knocknock.domain.waste.domain.Waste;
import com.knocknock.domain.waste.dto.WasteResDataDto;
import com.knocknock.domain.waste.dto.WasteReqDto;
        import com.knocknock.domain.waste.dto.WasteResDto;
import com.querydsl.jpa.support.QOracle10gDialect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
@Service
@Slf4j
@RequiredArgsConstructor
public class WasteServiceImpl implements WasteService{
    private final RestTemplate restTemplate = new RestTemplate();

    private final WasteRepository wasteRepository;

    @Value("${api.waste.url}")
    private String wasteApiUrl;

    @Value("${api.waste.secret-key}")
    private String wasteSecretKey;

    @Value("${api.kakao.url}")
    private String kakaoApiUrl;

    @Value("${api.kakao.secret-key}")
    private String kakaoSecretKey;
    @Override
    public List<WasteResDto> searchByAddress(WasteReqDto wasteReqDto) {
        String url = String.format(wasteApiUrl,wasteSecretKey);
        log.info("api 키: {} {}",wasteApiUrl,wasteSecretKey);
        WasteResDataDto reqDataDto = restTemplate.getForObject(url, WasteResDataDto.class);
        List<WasteResDto> responseList = new ArrayList();
        List<WasteResDto> data = reqDataDto.getData();
        for (WasteResDto dto : data) {
            if(dto.getAddress().substring(0,2).equals(wasteReqDto.getSido())
            && dto.getAddress().substring(3,3 + wasteReqDto.getGugun().length()).equals(wasteReqDto.getGugun())
            ){
                responseList.add(dto);
            }
        }
        if(responseList.size()==0){
            //kakao api 셋팅
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "KakaoAK " + kakaoSecretKey);
            HttpEntity request = new HttpEntity("",headers);
            String kakaoUrl = String.format(kakaoApiUrl,
                    wasteReqDto.getSido()
                    + " "
                    + wasteReqDto.getGugun());

            ResponseEntity<String> kakaoResponse = restTemplate.exchange(
                    kakaoUrl,
                    HttpMethod.GET,
                    request,
                    String.class
            );
            String resultString = kakaoResponse.getBody();
            JSONObject jsonObject = new JSONObject(resultString);
            JSONObject document = jsonObject.getJSONArray("documents").getJSONObject(0);
            Double currentx = document.getDouble("x");
            Double currenty = document.getDouble("y");

            List<Waste> wastes = wasteRepository.findAll();
            Waste minimunWasteEntity = null;
            Double minimumDistance = Double.MAX_VALUE;
            Double distance,x,y,absoluteDiffX,absoluteDiffY;
            for (Waste waste : wastes) {
                x = waste.getX();
                y = waste.getY();
                absoluteDiffX = Math.abs(currentx-x);
                absoluteDiffY = Math.abs(currenty-y);
                distance = Math.sqrt(
                                Math.pow(absoluteDiffX,2) +
                                Math.pow(absoluteDiffY,2));
                if(distance <= minimumDistance){
                    minimunWasteEntity = waste;
                    minimumDistance = distance;
                }
            }
            log.info("minimunWasteEntity : {}",minimunWasteEntity.toString());
            responseList.add(
                    WasteResDto.builder().
                            enterpriseName(minimunWasteEntity.getEnterpriseName()).
                            address(minimunWasteEntity.getAddress()).
                            telNo(minimunWasteEntity.getTelNo()).
                            wasteType(minimunWasteEntity.getWasteType()).
                            build()
            );
        }
//        log.info("추출 : {}",data.get(1).getAddress().substring(0,2));
//        log.info("추출 : {}",data.get(1).getAddress().substring(3,3 + wasteReqDto.getGugun().length()));
        log.info("시도 : {}",wasteReqDto.getSido());
        log.info("구군 : {}",wasteReqDto.getGugun());
        log.info("api 응답 : {}",responseList.toString());
        return responseList;
    }

    @Override
    @Transactional(readOnly = false)
    public void saveWasteEntity() {
        String wasteUrl = String.format(wasteApiUrl,wasteSecretKey);

        //waste api 셋팅
        String url = String.format(wasteApiUrl,wasteSecretKey);
        WasteResDataDto reqDataDto = restTemplate.getForObject(url, WasteResDataDto.class);

        //kakao api 셋팅
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "KakaoAK " + kakaoSecretKey);
        HttpEntity request = new HttpEntity("",headers);
        String kakaoUrl;
        List<WasteResDto> data = reqDataDto.getData();
        JSONObject jsonObject;
        JSONArray documents;
        JSONObject document;
        Double x;
        Double y;

        for (WasteResDto dto :data) {
            kakaoUrl = String.format(kakaoApiUrl,dto.getAddress());
            log.info("kakao 요청값 주소 : {}", kakaoUrl);
            ResponseEntity<String> kakaoResponse = restTemplate.exchange(
                    kakaoUrl,
                    HttpMethod.GET,
                    request,
                    String.class
            );
            String resultString = kakaoResponse.getBody();
            String resultUTF8String;
            try {
                resultUTF8String = new String (
                        resultString.getBytes("ISO-8859-1"),
                        "UTF-8"
                );
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            try {
                log.info("jsonString : {}", resultUTF8String);
                jsonObject = new JSONObject(resultUTF8String);
//                log.info("json : {}", jsonObject);
                documents = jsonObject.getJSONArray("documents");
                if(documents.length()==0) continue;
                document = documents.getJSONObject(0);
//                address = document.getJSONObject("address");
                x = document.getDouble("x");
                y = document.getDouble("y");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            log.info("x : {}", x);
            log.info("y : {}", y);
            Waste waste = Waste.builder()
                    .enterpriseName(dto.getEnterpriseName())
                    .address(dto.getAddress())
                    .telNo(dto.getTelNo())
                    .wasteType(dto.getWasteType())
                    .x(x)
                    .y(y)
                    .build();
            wasteRepository.save(waste);
        }

    }
}
