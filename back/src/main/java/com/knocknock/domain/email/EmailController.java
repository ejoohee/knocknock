package com.knocknock.domain.email;

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

    @PostMapping("/password")
    public ResponseEntity findPasswordMail(@RequestBody EmailPostDto emailPostDto) {
        EmailMessage emailMessage = EmailMessage.builder()
                .userEmail(emailPostDto.getEmail())
                .subject("[임시 비밀번호 발급]")
                .build();

        emailService.sendEmail(emailMessage, "password");

        return ResponseEntity.ok().build();
    }

    // 요청시 body로 인증번호 반환
    @PostMapping("/sign-up")
    public ResponseEntity sendSignUpMail(@RequestBody EmailPostDto emailPostDto) {
        EmailMessage emailMessage = EmailMessage.builder()
                .userEmail(emailPostDto.getEmail())
                .subject("[회원가입] 이메일 인증을 위한 코드 발송")
                .build();

        String code = emailService.sendEmail(emailMessage, "email");

        EmailResDto emailResDto = new EmailResDto();
        emailResDto.setCode(code);

        return ResponseEntity.ok(emailResDto);
    }


}
