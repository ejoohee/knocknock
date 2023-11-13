package com.knocknock.global.common.kepco;

import com.knocknock.domain.user.dto.response.FindPowerUsageHouseAvgResDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;

@Component
@Slf4j
public class KepcoAPIWebClient {

    private WebClient kepcoWebClient;

    @PostConstruct
    public void init() {
        kepcoWebClient = WebClient.builder()
                .baseUrl("https://bigdata.kepco.co.kr/openapi/v1/powerUsage/houseAve.do")
                .build();
    }

    /**
     * 가구 평균 전력 사용량 조회
     */
    public FindPowerUsageHouseAvgResDto findPowerUsageHouseAvg(Integer year, Integer month, Integer metroCd, Integer cityCd) {
        // 호출
        StringBuilder m = new StringBuilder();
        // 달이 10 미만이면 앞에 0 추가해야 함
        if(month < 10) m.append("0").append(month);
        else m.append(month);

        Mono<String> response = kepcoWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("year", year)
                        .queryParam("month", m)
                        .queryParam("metroCd", metroCd)
                        .queryParam("cityCd", cityCd)
                        .queryParam("apiKey", "2c6Tz02F144P97y0F9JuX495Zo8E7J14jYoCiAq5")
                        .build())
                .retrieve().bodyToMono(String.class);
        log.info("response ----> {}", response.block());
        try {
            // json 파싱
            StringReader stringReader = new StringReader(response.block());
            JsonReader jsonReader = Json.createReader(stringReader);
            JsonObject jsonObject = jsonReader.readObject();
            JsonArray jsonArray = jsonObject.getJsonArray("data");
            jsonObject = jsonArray.getJsonObject(0);
            // 필요한 정보 가져오기
            return FindPowerUsageHouseAvgResDto.builder()
                    .year(year)
                    .month(month)
                    .powerUsage(Float.valueOf(String.valueOf(jsonObject.getJsonNumber("powerUsage"))))
                    .bill(jsonObject.getInt("bill"))
                    .build();
        }catch (Exception e) {
            return FindPowerUsageHouseAvgResDto.builder()
                    .year(year)
                    .month(month)
                    .powerUsage(null)
                    .bill(null)
                    .build();
        }
    }

}
