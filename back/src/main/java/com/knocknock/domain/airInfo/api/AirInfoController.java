package com.knocknock.domain.airInfo.api;

import com.knocknock.domain.airInfo.dto.AirInfoResDto;
import com.knocknock.domain.airInfo.service.AirInfoService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "로그인 사용자의 실시간 대기 정보를 조회합니다.",
            description = "로그인 사용자의 정보를 이용해 실시간 대기정보를 반환합니다."
    )
    @GetMapping
    public ResponseEntity<AirInfoResDto> getAirInfoByRegion(@RequestHeader(ACCESS_TOKEN) String token) throws IOException {
        return ResponseEntity.ok(airInfoService.getAirInfoByRegion(token));
    }

}
