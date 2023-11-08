package com.knocknock.global.common.openapi;


import com.knocknock.domain.category.dao.CategoryRepository;
import com.knocknock.domain.category.domain.Category;
import com.knocknock.domain.model.dao.ModelRepository;
import com.knocknock.domain.model.domain.Model;
import com.knocknock.domain.model.dto.request.AddModelReqDto;
import com.knocknock.global.common.openapi.constants.CategoryURL;
import com.knocknock.global.common.openapi.constants.CategoryUsageType;
import com.knocknock.global.util.CrawlingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.TimeoutException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.json.*;
import javax.json.stream.JsonParsingException;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAPIWebClient {

    private WebClient authWebClient;

    private final CategoryRepository categoryRepository;
    private final ModelRepository modelRepository;


    private final CrawlingUtil crawlingUtil;
    private final int PER_PAGE = 100;


    @PostConstruct
    public void init() {
        // 한국에너지공단_에너지소비효율 등급 제품 정보 api 호출용
        authWebClient = WebClient.builder()
                .baseUrl("https://api.odcloud.kr/api/15083315/v1/uddi:e2de2c4d-e81b-421d-94bc-ad0f9cf70c44")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Infuser yB6pw3v/aprz92U8l42QpEk0LDn7RQF8i0VTp+VjfJ8TbUGnfHD/y53/qlG22MQd/NDKJGKATwxGRWPJjTujIQ==")
                .build();
    }

    // 가전제품 목록
    public List<Model> findModelList() throws InterruptedException, IOException {
        // api 요청
        // totalCount 받아서
        Mono<String> response = authWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("page", 1)
                        .queryParam("perPage", 1)
                        .build())
                .retrieve().bodyToMono(String.class);
        StringReader stringReader = new StringReader(response.block());
        JsonReader jsonReader = Json.createReader(stringReader);
        JsonObject jsonObject = jsonReader.readObject();
        int totalCount = jsonObject.getInt("totalCount");
        log.info("전체 totalCount ----> {}", totalCount);
        int pages = totalCount / PER_PAGE;
        if(totalCount % PER_PAGE != 0) pages++;
        log.info("전체 pages ----> {}", pages);
        // per page 단위로 끝까지 호출
        // 호출
        List<Model> modelList = new ArrayList<>();

        crawlingUtil.process();

        for (int i = 52; i <= pages; i++) {
            int finalI = i;
            response = authWebClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("page", finalI)
                            .queryParam("perPage", PER_PAGE)
                            .build())
                    .retrieve().bodyToMono(String.class);
            log.info("현재 page---------->{}", i);
//            modelList.addAll(parseModelListFromJson(response.block()));
            modelRepository.saveAll(parseModelListFromJson(response.block()));
        }

        crawlingUtil.close();


        return modelList;
    }

    private List<Model> parseModelListFromJson(String jsonResponse) throws InterruptedException, IOException {
        List<Model> modelList = new ArrayList<>();
        StringReader stringReader = new StringReader(jsonResponse);
        JsonReader jsonReader = Json.createReader(stringReader);
        JsonObject jsonObject = jsonReader.readObject();
        JsonArray jsonArray = jsonObject.getJsonArray("data");
        Category categoryEntity = null;
        for (JsonValue json : jsonArray) {
            // 삼성전자 제품에 해당하는 것만 저장
            JsonObject jsonObject1 = Json.createObjectBuilder()
                    .add("index", json)
                    .build();
//            log.info("json----->{}", jsonObject1);
            JsonObject jsonObject2 = jsonObject1.getJsonObject("index");
            // 모델명 추출
            String modelName = null;
            try{
                modelName = jsonObject2.getString("모델명");
            } catch (ClassCastException e) {
                continue;
            }
//            String modelName = jsonObject1.getString("모델명");
            // 카테고리 추출
            String category = jsonObject2.getString("품목명");
            CategoryURL categoryURL = CategoryURL.findtCategoryUrl(category);
            // 저장할 카테고리에 해당하지 않음
            if(categoryURL == null) continue;
//            log.info("현재 카테고리 -----------> {}", category);
            if(categoryEntity == null) categoryEntity = categoryRepository.findByName(categoryURL.getConvertedCategory()).get();
            else if(!categoryEntity.getName().equals(categoryURL.getConvertedCategory())) {
                // 최적화,,,,할 수 있나? 계속 EQUALS 부르는데
                categoryEntity = categoryRepository.findByName(categoryURL.getConvertedCategory()).get();
            }
            CategoryUsageType categoryUsageType = CategoryUsageType.findCategoryType(category);
            AddModelReqDto addModelReqDto = null;
            addModelReqDto = checkModelBrand(modelName, categoryURL.getUrl(), categoryUsageType);
            while(addModelReqDto != null && addModelReqDto.getError()) {
                log.info("언제돼?");
                addModelReqDto = checkModelBrand(modelName, categoryURL.getUrl(), categoryUsageType);
            }
            // 삼성전자 모델이 아님
            if(addModelReqDto == null) continue;

            log.info("현재 모델명 -----------> {}", modelName);
            log.info("삼성전자 모델");
            // 웹 크롤링
            String img = null;
            try {
                img = crawlingUtil.getImgLink(modelName);
            } catch (TimeoutException e) {
                log.error("존재하지 않는 상품!!!!!!!!!!!!!!!!!!!!!!!!");
                // not found 이미지
                continue;
            }
            addModelReqDto.setImg(crawlingUtil.getImgLink(modelName));
            // 월간이 아니라 연간 에너지 비용
            if(addModelReqDto.getCost() == null) addModelReqDto.setCost(jsonObject2.getInt("연간 에너지 비용"));
            float co2 = Float.valueOf(jsonObject2.getString("시간당 이산화탄소 배출량"));
            addModelReqDto.setCo2((int) co2);
            addModelReqDto.setGrade(Integer.valueOf(jsonObject2.getString("등급")));
            modelList.add(
                    Model.builder()
                            .category(categoryEntity)
                            .name(modelName)
                            .brand("삼성전자(주)")
                            .grade(addModelReqDto.getGrade())
                            .img(addModelReqDto.getImg())
                            .url(addModelReqDto.getUrl())
                            .usageValue1(addModelReqDto.getUsageValue1())
                            .usageValue2(addModelReqDto.getUsageValue2())
                            .usageValue3(addModelReqDto.getUsageValue3())
                            .co2(addModelReqDto.getCo2())
                            .cost(addModelReqDto.getCost())
                            // 나중에 출시년월 채울거면 추가
                            .releasedDate(null)
                            .build()
            );
        }
        return modelList;
    }

    // 삼성전자 모델이 맞으면 null이 아닌 객체를 반환
    public AddModelReqDto checkModelBrand(String modelName, String url, CategoryUsageType categoryUsageType) throws IOException {
        AddModelReqDto addModelReqDto = null;
        // 한국에너지공단_에너지소비효율 등급 제품 정보 api 호출용
//        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory("http://apis.data.go.kr/B553530/eep/");
//        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
//        noAuthWebClient = WebClient.builder().uriBuilderFactory(factory).baseUrl("http://apis.data.go.kr/B553530/eep/").build();
//        noAuthWebClient = WebClient.builder()
//                .baseUrl("http://apis.data.go.kr/B553530/eep/EEP_19_LIST")
//                .build();
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B553530/eep/"+url); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "="+"yB6pw3v%2Faprz92U8l42QpEk0LDn7RQF8i0VTp%2BVjfJ8TbUGnfHD%2Fy53%2FqlG22MQd%2FNDKJGKATwxGRWPJjTujIQ%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*검색건수(기본값: 10, 최소: 1, 최대: 100)*/
        urlBuilder.append("&" + URLEncoder.encode("apiType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*결과형식(xml/json)*/
        urlBuilder.append("&" + URLEncoder.encode("q2","UTF-8") + "=" + URLEncoder.encode(modelName, "UTF-8")); /*모델명*/
        urlBuilder.append("&" + URLEncoder.encode("q3","UTF-8") + "=" + URLEncoder.encode("삼성전자(주)", "UTF-8")); /*업체명칭*/
        URL url2 = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        try {
            conn.getResponseCode();
//            System.out.println("Response code: " + conn.getResponseCode());
        }catch(ConnectException e) {
            try {
                // 1초 대기
                Thread.sleep(1000); // 1000 밀리초 = 1초
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            // 에러 발생
            return AddModelReqDto.builder()
                    .error(true)
                    .build();
        }
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
//        log.info("응답-------->{}", sb.toString());
//        Mono<String> response = noAuthWebClient
//                .get()
//                .uri(urlBuilder.toString())
//                .retrieve().bodyToMono(String.class);
//        log.info("모델명으로 조회 응답값 -----> {}", response.block());
        StringReader stringReader = new StringReader(sb.toString());
        JsonReader jsonReader = null;
        JsonObject jsonObject = null;

        try{
            jsonReader = Json.createReader(stringReader);
            jsonObject = jsonReader.readObject();
        }catch (JsonParsingException e) {
            try {
                Thread.sleep(1000); // 1000 밀리초 = 1초
                return AddModelReqDto.builder()
                        .error(true)
                        .build();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }

        jsonObject = jsonObject.getJsonObject("response").getJsonObject("body");
        // totalCount가 0이면 삼성전자 제품이 아니라는 의미
        int tot = Integer.valueOf(jsonObject.getString("totalCount"));
        if(tot == 0) return null;
        urlBuilder = new StringBuilder("http://apis.data.go.kr/B553530/eep/"+url); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "="+"yB6pw3v%2Faprz92U8l42QpEk0LDn7RQF8i0VTp%2BVjfJ8TbUGnfHD%2Fy53%2FqlG22MQd%2FNDKJGKATwxGRWPJjTujIQ%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode(String.valueOf(tot), "UTF-8")); /*검색건수(기본값: 10, 최소: 1, 최대: 100)*/
        urlBuilder.append("&" + URLEncoder.encode("apiType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*결과형식(xml/json)*/
        urlBuilder.append("&" + URLEncoder.encode("q2","UTF-8") + "=" + URLEncoder.encode(modelName, "UTF-8")); /*모델명*/
        urlBuilder.append("&" + URLEncoder.encode("q3","UTF-8") + "=" + URLEncoder.encode("삼성전자(주)", "UTF-8")); /*업체명칭*/
        url2 = new URL(urlBuilder.toString());
        conn = (HttpURLConnection) url2.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        try {
            conn.getResponseCode();
//            System.out.println("Response code: " + conn.getResponseCode());
        }catch(ConnectException e) {
            try {
                // 1초 대기
                Thread.sleep(1000); // 1000 밀리초 = 1초
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            // 에러 발생
            return AddModelReqDto.builder()
                    .error(true)
                    .build();
        }
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        sb = new StringBuilder();
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
//        log.info("응답-------->{}", sb.toString());
//        Mono<String> response = noAuthWebClient
//                .get()
//                .uri(urlBuilder.toString())
//                .retrieve().bodyToMono(String.class);
//        log.info("모델명으로 조회 응답값 -----> {}", response.block());
        stringReader = new StringReader(sb.toString());
        jsonReader = null;
        jsonObject = null;

        try{
            jsonReader = Json.createReader(stringReader);
            jsonObject = jsonReader.readObject();
        }catch (JsonParsingException e) {
            try {
                Thread.sleep(1000); // 1000 밀리초 = 1초
                return AddModelReqDto.builder()
                        .error(true)
                        .build();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
        JsonArray jsonArray = jsonObject.getJsonObject("response").getJsonObject("body").getJsonObject("items").getJsonArray("item");
        System.out.println(jsonArray);
        for (JsonValue json : jsonArray) {
            JsonObject jsonObject1 = Json.createObjectBuilder()
                    .add("index", json)
                    .build();
            jsonObject1 = jsonObject1.getJsonObject("index");
            if(!jsonObject1.getString("MODEL_TERM").equals(modelName)) continue;
            Float usageValue1 = null;
            Float usageValue2 = null;
            Float usageValue3 = null;
            try {
                usageValue1 = categoryUsageType.getUsage1() == null ? null :  Float.valueOf(jsonObject1.getString(categoryUsageType.getUsage1()));
            } catch (NumberFormatException e) {
                log.error("usageValue1 값이 {},,,,,,,,,,,자료형 변환 불가능!!!!!!!!!!!!!!!", jsonObject1.getString(categoryUsageType.getUsage1()));
                String tmp = jsonObject1.getString(categoryUsageType.getUsage1());
                int l = tmp.length();
                for (int i = 0; i < l; i++) {
                    if(tmp.charAt(i) != '(') continue;
                    tmp = tmp.substring(0, i);
                    break;
                }
//                tmp = tmp.substring(0, tmp.length()-4);
                usageValue1 = Float.valueOf(tmp);
            }
            try {
                usageValue2 = categoryUsageType.getUsage2() == null ? null : Float.valueOf(jsonObject1.getString(categoryUsageType.getUsage2()));
            } catch (NumberFormatException e) {
                log.error("usageValue2 값이 {},,,,,,,,,,,자료형 변환 불가능!!!!!!!!!!!!!!!", jsonObject1.getString(categoryUsageType.getUsage2()));
                String tmp = jsonObject1.getString(categoryUsageType.getUsage2());
                int l = tmp.length();
                for (int i = 0; i < l; i++) {
                    if(tmp.charAt(i) != '(') continue;
                    tmp = tmp.substring(0, i);
                    break;
                }
                usageValue2 = Float.valueOf(tmp);
            }
            try {
                usageValue3 = categoryUsageType.getUsage3() == null  ? null : Float.valueOf(jsonObject1.getString(categoryUsageType.getUsage3()));
            } catch (NumberFormatException e) {
                log.error("usageValue3 값이 {},,,,,,,,,,,자료형 변환 불가능!!!!!!!!!!!!!!!", jsonObject1.getString(categoryUsageType.getUsage3()));
                String tmp = jsonObject1.getString(categoryUsageType.getUsage3());
                int l = tmp.length();
                for (int i = 0; i < l; i++) {
                    if(tmp.charAt(i) != '(') continue;
                    tmp = tmp.substring(0, i);
                    break;
                }
                usageValue3 = Float.valueOf(tmp);
            }
            addModelReqDto = AddModelReqDto.builder()
                    .error(false)
                    .usageValue1(usageValue1)
                    .usageValue2(usageValue2)
                    .usageValue3(usageValue3)
                    // 연간이 아니라 월간인 경우
                    .cost(categoryUsageType.getCost() == null  ? null : Integer.valueOf(jsonObject1.getString(categoryUsageType.getCost())))
                    .build();
        }
        return addModelReqDto;
    }


}
