package com.knocknock.domain.user.api;

import com.knocknock.domain.user.dto.password.FindPasswordReqDto;
import com.knocknock.domain.user.dto.password.PasswordReqDto;
import com.knocknock.domain.user.dto.password.UpdatePasswordReqDto;
import com.knocknock.domain.user.dto.request.GiroCodeReqDto;
import com.knocknock.domain.user.dto.request.LoginReqDto;
import com.knocknock.domain.user.dto.request.UpdateUserReqDto;
import com.knocknock.domain.user.dto.request.UserReqDto;
import com.knocknock.domain.user.dto.response.AdminUserResDto;
import com.knocknock.domain.user.dto.response.LoginResDto;
import com.knocknock.domain.user.dto.response.ReissueTokenResDto;
import com.knocknock.domain.user.dto.response.UserResDto;
import com.knocknock.domain.user.service.UserService;
import com.knocknock.global.dto.MessageDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final String ACCESS_TOKEN = "Authorization";
    private final String REFRESH_TOKEN = "Refresh-Token";
    private final UserService userService;

    @Operation(
            summary = "회원가입 서비스",
            description = "회원가입을 합니다."
    )
    @PostMapping("/sign-up")
    public ResponseEntity<MessageDto> signUp(@RequestBody @Valid UserReqDto userReqDto) {
        userService.signUp(userReqDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MessageDto.message("SIGN-UP SUCCESS"));
    }

    @Operation(
            summary = "로그인 서비스",
            description = "로그인 처리합니다."
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResDto> login(@RequestBody LoginReqDto loginReqDto) {
        return ResponseEntity.ok(userService.login(loginReqDto));
    }

    @Operation(
            summary = "로그아웃 서비스",
            description = "토큰이 존재한다면 로그아웃 처리합니다."
    )
    @PostMapping("/logout")
    public ResponseEntity<MessageDto> logout(@RequestHeader(ACCESS_TOKEN) String token) {
        userService.logout(token);

        return ResponseEntity.ok(MessageDto.message("LOGOUT SUCCESS"));
    }

    @Operation(
            summary = "비밀번호 변경",
            description = "기존 비밀번호, 새로운 비밀번호, 새로운 비밀번호 확인을 체크하여" +
                    "비밀번호를 변경합니다."
    )
    @PutMapping("/password")
    public ResponseEntity<MessageDto> updatePassword(@RequestBody UpdatePasswordReqDto reqDto, @RequestHeader(ACCESS_TOKEN) String token) {
        userService.updatePassword(reqDto, token);

        return ResponseEntity.ok(MessageDto.message("UPDATE PASSWORD SUCCESS"));
    }

    @Operation(
            summary = "비밀번호 찾기를 위한 이메일 & 닉네임 일치 검사",
            description = "비밀번호 찾기 이메일 발송 전에 이메일과 닉네임이 일치하는지 확인합니다."
    )
    @PostMapping("/password")
    public ResponseEntity<Boolean> findPassword(@RequestBody FindPasswordReqDto reqDto) {
        return ResponseEntity.ok(userService.findPassword(reqDto));
    }

    @Operation(
            summary = "서비스 사용전 비밀번호 확인",
            description = "일치하면 true, 불일치하면 false를 반환합니다. " +
                    "true반환 시에만 다음 서비스를 이용할 수 있습니다."
    )
    @GetMapping("/password")
    public ResponseEntity<Boolean> checkPassword(@RequestParam String password, @RequestHeader(ACCESS_TOKEN) String token) {
        return ResponseEntity.ok(userService.checkPassword(password, token));
    }

    @Operation(
            summary = "지로 코드 등록",
            description = "지로 코드를 등록합니다."
    )
    @PutMapping("/giro")
    public ResponseEntity<MessageDto> addGiroCode(@RequestBody GiroCodeReqDto reqDto, @RequestHeader(ACCESS_TOKEN) String token) {
        userService.addGiroCode(reqDto, token);

        return ResponseEntity.ok(MessageDto.message("ADD GIROCODE SUCCESS"));
    }


    @Operation(
            summary = "내 정보 수정",
            description = "나의 기본 정보를 수정합니다."
    )
    @PutMapping
    public ResponseEntity<UserResDto> updateUser(@RequestBody UpdateUserReqDto reqDto, @RequestHeader(ACCESS_TOKEN) String token) {
        return ResponseEntity.ok(userService.updateUser(reqDto, token));
    }


    @Operation(
            summary = "내 정보 조회",
            description = "나의 기본 정보를 조회합니다."
    )
    @GetMapping
    public ResponseEntity<UserResDto> findMyInfo(@RequestHeader(ACCESS_TOKEN) String token) {
        return ResponseEntity.ok(userService.findMyInfo(token));
    }


    @Operation(
            summary = "회원탈퇴",
            description = "스스로 회원탈퇴를 요청합니다."
    )
    @DeleteMapping
    public ResponseEntity<MessageDto> withdraw(@RequestHeader(ACCESS_TOKEN) String token) {
        userService.withdraw(token);
        return ResponseEntity.ok(MessageDto.message("WITHDRAW SUCCESS"));
    }

    @Operation(
            summary = "토큰 재발급",
            description = "토큰을 재발급 받습니다."
    )
    @PostMapping("/reissue-token")
    public ResponseEntity<ReissueTokenResDto> reissueToken(@RequestHeader("Authorization") String accessToken, @RequestHeader("Refresh-Token") String refreshToken) {
        return ResponseEntity.ok(userService.reissueToken(accessToken, refreshToken));
    }


    // 여기부터 관리자
    @Operation(
            summary = "전체 회원 목록 조회",
            description = "관리자 회원으로 로그인하여 전체 회원 목록을 조회합니다."
    )
    @GetMapping("/admin/all")
    public ResponseEntity<List<AdminUserResDto>> findUserList(@RequestHeader(ACCESS_TOKEN) String token) {
        return ResponseEntity.ok(userService.findUserList(token));
    }

    @Operation(
            summary = "회원 조회",
            description = "관리자 회원으로 로그인하여 특정 회원의 정보를 조회합니다."
    )
    @GetMapping("/admin/{userId}")
    public ResponseEntity<AdminUserResDto> findUser(@PathVariable Long userId, @RequestHeader(ACCESS_TOKEN) String token) {
        return ResponseEntity.ok(userService.findUser(userId, token));
    }

//    @Operation(
//            summary = "회원 조건별 검색",
//            description = "관리자 회원으로 로그인하여 조건별로 회원을 검색합니다."
//    )
//    @GetMapping("/admin/~~~~")
//    public ResponseEntity<List<AdminUserResDto>> findUserByCondition(@RequestParam , @RequestHeader(ACCESS_TOKEN) String token) {
//        return ResponseEntity.ok(userService.findUserByCondition());
//    }

    @Operation(
            summary = "회원 강제탈퇴",
            description = "관리자 회원으로 로그인하여 특정 회원을 강제탈퇴 처리합니다."
    )
    @DeleteMapping("/admin/{userId}")
    public ResponseEntity<MessageDto> deleteUser(@PathVariable Long userId, @RequestHeader(ACCESS_TOKEN) String token) {
        userService.deleteUser(userId, token);
        return ResponseEntity.ok(MessageDto.message("DELETE SUCCESS"));
    }




}
