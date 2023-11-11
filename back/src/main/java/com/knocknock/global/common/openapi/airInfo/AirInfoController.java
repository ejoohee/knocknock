package com.knocknock.global.common.openapi.airInfo;

import com.knocknock.global.common.openapi.airInfo.dto.AirInfoReqDto;
import com.knocknock.global.common.openapi.airInfo.dto.AirInfoResDto;
import com.knocknock.global.common.openapi.airInfo.dto.TmPointDto;
import com.knocknock.global.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/air-info")
public class AirInfoController {

//    private final String ACCESS_TOKEN = "accessToken";
    private final String ACCESS_TOKEN = "Authorization";
    private final AirInfoService airInfoService;

    @PutMapping
    public ResponseEntity<MessageDto> saveAirStation(@RequestHeader(ACCESS_TOKEN) String token) throws IOException {
        airInfoService.connectLogic(token);

        return ResponseEntity.ok(MessageDto.message("AIR-STATION UPDATE SUCCESS"));
    }

    @GetMapping
    public ResponseEntity<AirInfoResDto> getAirInfoByRegion(AirInfoReqDto reqDto) throws IOException {
        return ResponseEntity.ok(airInfoService.getAirInfoByRegion(reqDto));
    }

    @GetMapping("/tm-point")
    public ResponseEntity<TmPointDto> getTmPoint(String address) throws IOException {
        return ResponseEntity.ok(airInfoService.getTmPoint(address));
    }

    @GetMapping("/station-name")
    public ResponseEntity<String> getStationName(TmPointDto pointDto) throws IOException {
        return ResponseEntity.ok(airInfoService.getStationName(pointDto));
    }

    @GetMapping("/pick/dong")
    public ResponseEntity<String> pickDongNameByAddress(String address) throws IOException {
        return ResponseEntity.ok(airInfoService.pickDongNameByAddress(address));
    }

}
