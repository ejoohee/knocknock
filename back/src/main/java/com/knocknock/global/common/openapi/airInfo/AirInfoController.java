package com.knocknock.global.common.openapi.airInfo;

import com.knocknock.global.common.openapi.airInfo.dto.AirInfoReqDto;
import com.knocknock.global.common.openapi.airInfo.dto.AirInfoResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/air-info")
public class AirInfoController {

    private final String ACCESS_TOKEN = "accessToken";
    //    private final String ACCESS_TOKEN = "Authorization";
    private final AirInfoService airInfoService;

    @GetMapping
    public ResponseEntity<AirInfoResDto> getAirInfoByRegion(AirInfoReqDto reqDto) throws IOException {
        return ResponseEntity.ok(airInfoService.getAirInfoByRegion(reqDto));
    }


}
