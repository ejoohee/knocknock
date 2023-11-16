package com.knocknock.global.common.external.ocr;


import com.knocknock.global.common.external.constants.ExternalApiBaseUrl;
import com.knocknock.global.common.external.ocr.dto.ImageInfoDto;
import com.knocknock.global.common.external.ocr.dto.ImageRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
public class OCRAPIWebClient {

    private WebClient ocrWebClient;

    @Value("${spring.ocr.secret}")
    private String ocrSecret;

    @PostConstruct
    public void init() {
        ocrWebClient = WebClient.builder()
                .baseUrl(ExternalApiBaseUrl.OCR_TEXT.getUrl())
                .defaultHeader("X-OCR-SECRET", ocrSecret)
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
