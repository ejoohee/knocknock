package com.knocknock.domain.user.api;

import com.knocknock.domain.user.dto.request.CheckGoogleReqDto;
import com.knocknock.domain.user.dto.request.GoogleLoginReqDto;
import com.knocknock.domain.user.dto.request.UpdateAddressReqDto;
import com.knocknock.domain.user.dto.response.SocialLoginResDto;
import com.knocknock.domain.user.dto.response.UpdateAddressResDto;
import com.knocknock.domain.user.service.UserSocialService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/user", produces = "application/json")
public class UserSocialController {

    private final String ACCESS_TOKEN = "Authorization";
    private final UserSocialService userSocialService;

//    @Operation(
//            summary = "소셜 로그인 및 회원가입 서비스[백엔드용]",
//            description = "백엔드용 소셜 로그인 및 회원가입을 합니다."
//    )
//    @GetMapping("/login/google")
//    public ResponseEntity<SocialLoginResDto> googleBackLogin(@RequestParam String code) {
//        return ResponseEntity.ok(userSocialService.socialLogin(code));
//    }

    @Operation(
            summary = "소셜 로그인 및 회원가입 서비스[프론트용]",
            description = "프론트용 소셜 로그인 및 회원가입을 합니다."
    )
    @PostMapping("/login/google")
    public ResponseEntity<SocialLoginResDto> googleFrontLogin(@RequestBody GoogleLoginReqDto reqDto) {
        return ResponseEntity.ok(userSocialService.socialFrontLogin(reqDto));
    }

    @Operation(
            summary = "소셜 이메일 확인 서비스[프론트용]",
            description = "소셜 로그인 시 DB에 존재하는 회원인지 확인합니다."
    )
    @PostMapping("/check/google")
    public ResponseEntity<Void> checkGoogleLogin(@RequestBody CheckGoogleReqDto reqDto) {
        userSocialService.checkSocialLogin(reqDto);
        // 예외를 던지지 않으면 유저가 존재하는 것이므로 200 OK 반환
        return ResponseEntity.ok().build();
    }


    @Operation(
            summary = "소셜 주소 설정",
            description = "소셜 로그인 주소를 설정 합니다."
    )
    @PutMapping("/address")
    public ResponseEntity<UpdateAddressResDto> updateAddress(@RequestBody UpdateAddressReqDto reqDto, @RequestHeader(ACCESS_TOKEN) String token) {
        return ResponseEntity.ok(userSocialService.updateAddress(reqDto, token));
    }
}
