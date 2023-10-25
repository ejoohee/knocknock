package com.knocknock.domain.email.service;

import com.knocknock.domain.email.domain.EmailMessage;
import com.knocknock.domain.email.dto.EmailCodeReqDto;
import com.knocknock.domain.email.dto.EmailCodeResDto;
import com.knocknock.domain.email.dto.EmailPostDto;
import com.knocknock.domain.email.exception.EmailCodeException;
import com.knocknock.domain.user.dao.UserRepository;
import com.knocknock.domain.user.exception.UserException;
import com.knocknock.domain.user.exception.UserExceptionMessage;
import com.knocknock.domain.user.service.UserService;
import com.knocknock.global.exception.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate; // 이메일 코드 저장용
    private final SpringTemplateEngine templateEngine; // 타임리프 이용


    @Transactional
    @Override
    public EmailCodeResDto sendEmail(EmailPostDto emailPostDto, String type) {
        String email = emailPostDto.getEmail();

        // 근데 이거 프론트에서 처리하긴하는데 지워도되나?
        // 이메일 중복검사 --> 비번 변경일땐 체크안하고, 회원가입일떄만 체크
//        if(type.equals("email") && checkEmail(emailPostDto)) {
//            log.error("[이메일 발신 - 회원가입] 이메일 중복. 회원가입 불가");
//            throw new UserException(UserExceptionMessage.EMAIL_DUPLICATED.getMessage());
//        }

        log.info("[이메일 발신] 발신 요청. email : {}, type : {}", email, type);

        // 요청 타입별 이메일 제목 및 수신자 설정
        EmailMessage emailMessage = setEmailSubject(email, type);

        // 임시 코드 발급
        String authCode = createCode();

        // 메일발송을 위한 메일 생성
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        // 패스워드 찾기면 해당 유저의 비밀번호를 임시 비밀번호로 변경
        if(type.equals("password"))
            userService.updateTempPassword(email, authCode);

        try {
            // 메일 내용 설정
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setTo(email);  // 메일 수신자 설정
            helper.setSubject(emailMessage.getSubject()); // 메일 제목 설정
            helper.setText(setContext(authCode, type), true);  // 메일 내용, HTML 여부(타임리프를 사용해서 true)

            // 메일 발송 시도
            log.info("[이메일 발신] 이메일 작성 완료. 발송 시도");
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("[이메일 발신] 발신 실패. ERROR 발생.");
            throw new EmailCodeException("이메일 발신 실패 에러");
        }

        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        // 유효시간(10분) 동안 {email, authKey} 저장
        valueOperations.set(email, authCode, 60 * 10L, TimeUnit.SECONDS);

        log.info("[이메일 발신] 이메일 발신 성공! email : {}, code : {}", email, authCode);
        return EmailCodeResDto.builder()
                .emailCode(authCode)
                .build();
    }

    /**
     * 발송된 이메일 인증 코드와, 작성한 인증 코드 일치 체크
     */
    @Transactional
    @Override
    public Boolean checkEmailCode(EmailCodeReqDto emailCodeReqDto) {
        log.info("[이메일 인증 코드 유효검사] 검사 요청. email : {}, code : {}", emailCodeReqDto.getEmail(), emailCodeReqDto.getCode());

        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        // email 키로 code value 반환
        String originCode = valueOperations.get(emailCodeReqDto.getEmail()).toString();

        if(originCode == null){
            log.error("[인증 코드 일치 체크] 인증 코드가 존재하지 않습니다.");
            throw new NotFoundException("해당 이메일로 유효한 인증 코드가 존재하지 않습니다.");
        }

        if(originCode.equals(emailCodeReqDto.getCode())) {
            log.info("[인증 코드 일치 체크] 코드 일치. 이메일 인증 완료.");

            // 레디스에 저장해놨던 임시 코드 삭제 후
            valueOperations.getOperations().delete(emailCodeReqDto.getEmail());

            // 인증 완료를 저장
            valueOperations.set(emailCodeReqDto.getEmail(), "인증완료", 60 * 10L, TimeUnit.SECONDS);

            return true;
        }

        // 이거 코드 잘못입력한 후 false 나오고 나면
        // 그이후에 맞는 코드 작성해도 계속 false 나오는데 이게맞나,,,?
        return false;
    }

    /**
     * 회원가입 전 이메일 중복검사
     * 중복이 아니라서 회원가입이 가능하면 true를 반환하고,
     * 중복이면 400에러를 호출합니다.
     */
    @Transactional
    @Override
    public Boolean checkEmail(String email) {
        log.info("[이메일 중복 검사] 중복 검사 요청. email : {}", email);

        if(userRepository.existsByEmail(email)){
            log.error("[이메일 중복 검사] 이메일 중복. 회원가입 불가.");
            throw new UserException(UserExceptionMessage.EMAIL_DUPLICATED.getMessage());
        }

        log.info("[이메일 중복 검사] 중복 검사 완료. 중복없어 회원가입 가능!");
        return true;
    }

    /**
     * 이메일 제목 설정
     * 요청 타입에 따라 이메일 제목을 설정합니다.
     * 수신자 이메일도 설정합니다.
     */
    private EmailMessage setEmailSubject(String email, String type) {
        log.info("[이메일 제목 설정] type : {}", type);

        String subject = "";

        // 비밀번호 찾기라면
        if(type.equals("password")) {
            subject = "[KnocknocK] 【비밀번호 찾기】 임시 비밀번호 발급";
        } else {
            subject = "[KnocknocK] 【회원가입】 이메일 인증 코드 발송";
        }

        return EmailMessage.builder()
                .userEmail(email)
                .subject(subject)
                .build();
    }

    /**
     * 인증번호 및 임시 비밀번호 생성
     * 랜덤으로 코드를 생성합니다.
     */
    private String createCode() {
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
    private String setContext(String code, String type) {
        Context context = new Context();
        context.setVariable("code", code);

        return templateEngine.process(type, context);
    }

}
