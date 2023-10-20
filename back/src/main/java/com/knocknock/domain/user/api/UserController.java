package com.knocknock.domain.user.api;

import com.knocknock.domain.user.dto.password.FindPasswordReqDto;
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

    @PostMapping("/login")
    public ResponseEntity<LoginResDto> login(@RequestBody LoginReqDto loginReqDto) {
        return ResponseEntity.ok(userService.login(loginReqDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageDto> logout(@RequestHeader(ACCESS_TOKEN) String token) {
        userService.logout(token);

        return ResponseEntity.ok(MessageDto.message("LOGOUT SUCCESS"));
    }

    @PutMapping("/password")
    public ResponseEntity<MessageDto> updatePassword(@RequestBody UpdatePasswordReqDto reqDto, @RequestHeader(ACCESS_TOKEN) String token) {
        userService.updatePassword(reqDto, token);

        return ResponseEntity.ok(MessageDto.message("UPDATE PASSWORD SUCCESS"));
    }

    @PostMapping("/password")
    public ResponseEntity<MessageDto> findPassword(@RequestBody FindPasswordReqDto reqDto) {
        userService.findPassword(reqDto);

        return ResponseEntity.ok(MessageDto.message("PASSWORD-MAIL SENT SUCCESS"));
    }


}
