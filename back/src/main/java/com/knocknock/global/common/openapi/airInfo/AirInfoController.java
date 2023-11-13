package com.knocknock.global.common.openapi.airInfo;

import com.knocknock.global.common.openapi.airInfo.dto.AirInfoResDto;
import com.knocknock.global.common.openapi.airInfo.dto.AirStationDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/air-info")
public class AirInfoController {

//    private final String ACCESS_TOKEN = "accessToken";
    private final String ACCESS_TOKEN = "Authorization";
    private final AirInfoService airInfoService;


//    @Operation(
//            summary = "로그인 사용자의 대기 측정소를 자동으로 찾아서 저장합니다. --> 백에서 자동 실행",
//            description = "회원가입시 또는 주소 정보가 업데이트 될 때마다 자동 실행합니다."
//    )
//    @PutMapping
//    public ResponseEntity<AirStationDto> saveAirStation(String email) throws IOException {
//        return ResponseEntity.ok(airInfoService.saveAirStation(email));
//    }

    @Operation(
            summary = "로그인 사용자의 실시간 대기 정보를 조회합니다.",
            description = "로그인 사용자의 정보를 이용해 실시간 대기정보를 반환합니다."
    )
    @GetMapping
    public ResponseEntity<AirInfoResDto> getAirInfoByRegion(@RequestHeader(ACCESS_TOKEN) String token) throws IOException {
        return ResponseEntity.ok(airInfoService.getAirInfoByRegion(token));
    }

//    @Operation(
//            summary = "시도 정보로 대기 측정소 목록을 불러옵니다.(테스트중)",
//            description = "시도 정보를 입력하여 해당 지역의 측정소 목록을 반환합니다."
//    )
//    @GetMapping("/selectList")
//    public ResponseEntity<List<StationType>> findStationListByRegion(String region) {
//        return ResponseEntity.ok(airInfoService.findStationListByRegion(region));
//    }
//
//    @Operation(
//            summary = "시군구 정보로 특정 측정소를 반환합니다.(테스트중)",
//            description = "시군구 정보를 입력하여 일치하는 측정소를 반환합니다."
//    )
//    @GetMapping("/selectOne")
//    public ResponseEntity<StationType> findStationByRegionDetail(List<StationType> stationList, String regionDetail) {
//        return ResponseEntity.ok(airInfoService.findStationByRegionDetail(stationList, regionDetail));
//    }
//
//    @Operation(
//            summary = "로그인 사용자가 대기 측정소를 직접 선택하여 저장합니다.(테스트중)",
//            description = "로그인 사용자가 측정소 목록중에 희망하는 측정소를 직접 선택할 수 있습니다."
//    )
//    @PutMapping("/selectOne")
//    public ResponseEntity<AirStationDto> selectAirStation(@RequestHeader(ACCESS_TOKEN) String token, StationType stationType) {
//        return ResponseEntity.ok(airInfoService.selectAirStation(token, stationType));
//    }

//    @GetMapping("/tm-point")
//    public ResponseEntity<TmPointDto> getTmPoint(String address) throws IOException {
//        return ResponseEntity.ok(airInfoService.getTmPoint(address));
//    }
//
//    @GetMapping("/station-name")
//    public ResponseEntity<String> getStationName(TmPointDto pointDto) throws IOException {
//        return ResponseEntity.ok(airInfoService.getStationName(pointDto));
//    }
//
//    @GetMapping("/pick/dong")
//    public ResponseEntity<String> pickDongNameByAddress(String address) throws IOException {
//        return ResponseEntity.ok(airInfoService.pickDongNameByAddress(address));
//    }

}
