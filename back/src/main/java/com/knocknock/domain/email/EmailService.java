package com.knocknock.domain.email;

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

    public String sendEmail(EmailMessage emailMessage, String type) {
        String authNum = createCode();

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        // 패스워드 찾기면 해당 유저의 비밀번호를 임시 비밀번호로 변경
        if(type.equals("password"))
            userService.updateTempPassword(emailMessage.getUserEmail(), authNum);

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setTo(emailMessage.getUserEmail());  // 메일 수신자 설정
            helper.setSubject(emailMessage.getSubject()); // 메일 제목 설정
            helper.setText(setContext(authNum, type), true);  // 메일 내용, HTML 여부

            javaMailSender.send(mimeMessage);
            log.info("[이메일 코드 발신] 이메일 발신 성공! email : {}", emailMessage.getUserEmail());

            return authNum;
        } catch (MessagingException e) {
            log.error("[이메일 코드 발신] 실패");
//            throw new EmailCodeException("이메일 코드 발신 실패");
            throw new RuntimeException(e);
        }

    }

    /**
     * 인증번호 및 임시 비밀번호 생성
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
