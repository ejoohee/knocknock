package com.knocknock.domain.email.api;

import com.knocknock.domain.email.dto.EmailCodeResDto;
import com.knocknock.domain.email.dto.EmailPostDto;
import com.knocknock.domain.email.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/email")
@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @Operation(
            summary = "회원가입 이메일 인증 코드 발신",
            description = "회원가입시 이메일 인증 코드 유효 검사를 위한 이메일 인증 코드 발신입니다."
    )
    @PostMapping("/sign-up")
    public ResponseEntity<EmailCodeResDto> sendSignUpMail(@RequestBody EmailPostDto emailPostDto) {
        return ResponseEntity.ok(emailService.sendEmail(emailPostDto, "email"));
    }

    @Operation(
            summary = "임시 비밀번호 발급",
            description = "비밀번호 찾기를 위한 임시 비밀번호 발급입니다."
    )
    @PostMapping("/password")
    public ResponseEntity<EmailCodeResDto> findPasswordMail(@RequestBody EmailPostDto emailPostDto) {
        return ResponseEntity.ok(emailService.sendEmail(emailPostDto, "password"));
    }

}
