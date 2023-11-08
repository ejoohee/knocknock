package com.knocknock.global.common.ocr;


import com.knocknock.global.common.ocr.dto.ImageInfoDto;
import com.knocknock.global.common.ocr.dto.ImageRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.json.*;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OCRAPIWebClinet {

    private WebClient ocrWebClient;

    @PostConstruct
    public void init() {
        ocrWebClient = WebClient.builder()
                .baseUrl("https://wjibbfej6w.apigw.ntruss.com/custom/v1/25714/ae22652173a41a47c1a7d8825811303a9a2c989203f7d8b3857aadefee96dcb8/general")
                .defaultHeader("X-OCR-SECRET", "U21rWm1WZ3R3cVFKdnlVckVnaGhaY3BYbE1oemJZem8=")
                .build();
    }

    /**
     * 이미지에서 텍스트 추출
     * @param img
     * @return
     */
    public List<String> getTextListFromImg(String img) {
        long currentTimestamp = System.currentTimeMillis() / 1000;
        List<ImageInfoDto> imageInfoDtoList = new ArrayList<>();
        imageInfoDtoList.add(
                ImageInfoDto.builder()
                        .format("png")
                        .name("medium")
                        .data(img)
                        .build()
        );
        Mono<String> response = ocrWebClient
                .post()
                .body(BodyInserters.fromValue(ImageRequestDto.builder()
                                .images(imageInfoDtoList)
                                .lang("ko")
                                .requestId("string")
                                .resultType("string")
                                .timestamp(currentTimestamp)
                                .version("V2")
                        .build()))
                .retrieve().bodyToMono(String.class);
        log.info("OCR response ------> {}", response.block());
        StringReader stringReader = new StringReader(response.block());
        JsonReader jsonReader = Json.createReader(stringReader);
        JsonObject jsonObject = jsonReader.readObject();
        JsonArray jsonArray = jsonObject.getJsonArray("images").getJsonObject(0).getJsonArray("fields");
        return jsonArray.stream().map(
                (jsonValue) -> {
                    JsonObject jsonObject1 = Json.createObjectBuilder()
                            .add("index", jsonValue)
                            .build();
                    JsonObject jsonObject2 = jsonObject1.getJsonObject("index");
                    log.info("inferText ------> {}", jsonObject2.getString("inferText"));
                    return jsonObject2.getString("inferText");
                }).collect(Collectors.toList());
    }

}
