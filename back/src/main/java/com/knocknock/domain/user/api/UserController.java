package com.knocknock.domain.user.api;

import com.knocknock.domain.user.dto.password.FindPasswordReqDto;
import com.knocknock.domain.user.dto.password.PasswordReqDto;
import com.knocknock.domain.user.dto.password.UpdatePasswordReqDto;
import com.knocknock.domain.user.dto.request.LoginReqDto;
import com.knocknock.domain.user.dto.request.UserReqDto;
import com.knocknock.domain.user.dto.response.LoginResDto;
import com.knocknock.domain.user.service.UserService;
import com.knocknock.global.dto.MessageDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final String ACCESS_TOKEN = "AccessToken";
    private final String REFRESH_TOKEN = "RefreshToken";
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
        System.out.println("콘트롤러");
        return ResponseEntity.ok(userService.checkPassword(password, token));
    }



}
