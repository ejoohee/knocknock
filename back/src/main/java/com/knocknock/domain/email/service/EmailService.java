package com.knocknock.domain.email.service;

import com.knocknock.domain.email.domain.EmailMessage;
import com.knocknock.domain.email.dto.EmailCodeResDto;
import com.knocknock.domain.email.dto.EmailPostDto;
import com.knocknock.domain.email.exception.EmailCodeException;
import com.knocknock.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final SpringTemplateEngine templateEngine; // 타임리프 이용
    private final JavaMailSender javaMailSender;
    private final UserService userService;

    public EmailCodeResDto sendEmail(EmailPostDto emailPostDto, String type) {
        log.info("[이메일 발신] 발신 요청. email : {}, type : {}", emailPostDto.getEmail(), type);

        // 요청 타입별 이메일 제목 및 수신자 설정
        EmailMessage emailMessage = setEmailSubject(emailPostDto, type);

        // 임시 코드 발급
        String authNum = createCode();

        // 메일발송을 위한 메일 생성
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        // 패스워드 찾기면 해당 유저의 비밀번호를 임시 비밀번호로 변경
        if(type.equals("password"))
            userService.updateTempPassword(emailMessage.getUserEmail(), authNum);

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setTo(emailMessage.getUserEmail());  // 메일 수신자 설정
            helper.setSubject(emailMessage.getSubject()); // 메일 제목 설정
            helper.setText(setContext(authNum, type), true);  // 메일 내용, HTML 여부(타임리프를 사용해서 true)

            javaMailSender.send(mimeMessage);
            log.info("[이메일 발신] 이메일 발신 성공! email : {}, code : {}", emailMessage.getUserEmail(), authNum);

            return EmailCodeResDto.builder()
                    .emailCode(authNum)
                    .build();
        } catch (MessagingException e) {
            log.error("[이메일 발신] 발신 실패. ERROR 발생.");
            throw new EmailCodeException("이메일 발신 실패 에러");
        }

    }

    /**
     * 이메일 제목 설정
     * 요청 타입에 따라 이메일 제목을 설정합니다.
     * 수신자 이메일도 설정합니다.
     */
    public EmailMessage setEmailSubject(EmailPostDto emailPostDto, String type) {
        log.info("[이메일 제목 설정] type : {}", type);

        String subject = "";

        // 비밀번호 찾기라면
        if(type.equals("password")) {
            subject = "[KnocknocK] (비밀번호 찾기) 임시 비밀번호 발급";
        } else {
            subject = "[KnocknocK] (회원가입) 이메일 인증 코드 발송";
        }

        return EmailMessage.builder()
                .userEmail(emailPostDto.getEmail())
                .subject(subject)
                .build();
    }

    /**
     * 인증번호 및 임시 비밀번호 생성
     * 랜덤으로 코드를 생성합니다.
     */
    public String createCode() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for(int i=0; i<8; i++) {
            int idx = random.nextInt(4);

            switch (idx) {
                case 0:
                    key.append((char)((int)random.nextInt(26) + 97));
                    break;
                case 1:
                    key.append((char)((int)random.nextInt(26) + 65));
                    break;
                default:
                    key.append(random.nextInt(9));
            }
        }

        return key.toString();
    }

    /**
     * Thymeleaf를 통한 html 적용
     */
    public String setContext(String code, String type) {
        Context context = new Context();
        context.setVariable("code", code);

        return templateEngine.process(type, context);
    }

}
