package com.knocknock.domain.user.api;

import com.knocknock.domain.user.dto.request.UserReqDto;
import com.knocknock.domain.user.service.UserService;
import com.knocknock.global.dto.MessageDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "회원가입 서비스",
            description = "회원가입을 합니다."
    )
    @PostMapping
    public ResponseEntity<MessageDto> signUp(@RequestBody @Valid UserReqDto userReqDto) {
        userService.signUp(userReqDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MessageDto.message("SIGN-UP SUCCESS"));
    }



}
