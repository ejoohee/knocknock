package com.knocknock.domain.user.service;

import com.knocknock.domain.user.dao.LogoutAccessTokenRedisRepository;
import com.knocknock.domain.user.dao.RefreshTokenRedisRepository;
import com.knocknock.domain.user.dao.UserRepository;
import com.knocknock.domain.user.domain.RefreshToken;
import com.knocknock.domain.user.domain.Users;
import com.knocknock.domain.user.dto.password.FindPasswordReqDto;
import com.knocknock.domain.user.dto.password.PasswordReqDto;
import com.knocknock.domain.user.dto.password.UpdatePasswordRepDto;
import com.knocknock.domain.user.dto.request.*;
import com.knocknock.domain.user.dto.response.AdminUserResDto;
import com.knocknock.domain.user.dto.response.LoginResDto;
import com.knocknock.domain.user.dto.response.ReissueTokenResDto;
import com.knocknock.domain.user.dto.response.UserResDto;
import com.knocknock.domain.user.exception.UserExceptionMessage;
import com.knocknock.domain.user.exception.UserNotFoundException;
import com.knocknock.global.common.jwt.JwtExpirationEnum;
import com.knocknock.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenRedisRepository refreshTokenRepository;
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRepository;
    private final JavaMailSender javaMailSender;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 회원가입 정보의 유효성을 확인합니다.
     * 유효하면 true / 에러시 false
     */
    public Boolean checkSignupInfo(UserReqDto userReqDto) {
        if(userReqDto.getEmail() == null || userReqDto.getEmail().equals("") ||
           userReqDto.getPassword() == null || userReqDto.getPassword().equals("") ||
           userReqDto.getNickname() == null || userReqDto.getNickname().equals("")) {
            return false;
        }

        return true;
    }

    @Transactional
    @Override
    public void signUp(UserReqDto userReqDto) {
        // 회원가입 정보 유효성 확인
        if(!checkSignupInfo(userReqDto)) {
            log.error("[유저 회원가입] 회원가입 정보 유효성 불일치.");
            throw new IllegalArgumentException(UserExceptionMessage.SIGN_UP_NOT_VALID.getMessage());
        }

        log.info("[유저 회원가입] 회원가입 정보 유효성 일치. 다음 로직 실행");
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        // 이메일 인증 여부 확인
        String checkResult = (String) valueOperations.get(userReqDto.getEmail());
        if(checkResult == null || !checkResult.equals("이메일 인증 완료"))
            throw new IllegalArgumentException(UserExceptionMessage.EMAIL_CHECK_FAILED.getMessage());

        log.info("[유저 회원가입] 이메일 인증 완료!");

        // 패스워드 암호화
        userReqDto.setPassword(passwordEncoder.encode(userReqDto.getPassword()));
        log.info("[유저 회원가입] 패스워드 암호화 완료.");

        Users user = userRepository.save(userReqDto.dtoToEntity());
        log.info("[유저 회원가입] 유저 생성 완료!!! 회원가입이 완료되었습니다!");
    }

    @Transactional
    @Override
    public LoginResDto login(LoginReqDto loginReqDto) {
        String email = loginReqDto.getEmail();

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[유저 로그인] 유저를 찾을 수 없습니다.");
                    return new UserNotFoundException(UserExceptionMessage.USER_NOT_FOUND.getMessage());
                });

        if(!passwordEncoder.matches(loginReqDto.getPassword(), user.getPassword())) {
            log.error("[유저 로그인] 패스워드 불일치");
            throw new IllegalArgumentException(UserExceptionMessage.LOGIN_PASSWORD_ERROR.getMessage());
        }

        log.info("[유저 로그인] 로그인 성공! email : {}", email);

        // 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(email);
        String refreshToken = jwtUtil.generateRefreshToken(email);

        // Redis에 refreshToken 저장
        // 회원의 이메일(ID)을 키로 저장
        refreshTokenRepository.save(RefreshToken.builder()
                .email(email)
                .refreshToken(refreshToken)
                .expiration(JwtExpirationEnum.REFRESH_TOKEN_EXPIRATION_TIME.getValue() / 1000)
                .build());

        return LoginResDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .nickname(user.getNickname())
                .build();
    }

    @Override
    public void logout(String email, String token) {

    }

    @Override
    public void findPassword(FindPasswordReqDto findPasswordReqDto, String token) {

    }

    @Override
    public Boolean checkPassword(PasswordReqDto passwordReqDto, String token) {
        return null;
    }

    @Override
    public void updatePassword(UpdatePasswordRepDto updatePasswordRepDto, String token) {

    }

    @Override
    public UserResDto updateUser(UpdateUserReqDto updateUserReqDto, String token) {
        return null;
    }

    @Override
    public void withdraw(Boolean checkPassword, String token) {

    }

    @Override
    public UserResDto findMyInfo(String token) {
        return null;
    }

    @Override
    public ReissueTokenResDto reissueToken(String accessToken, String refreshToken) {
        return null;
    }

    @Override
    public void deleteUser(Long userId, String token) {
        // 관리자 체크

    }

    @Override
    public List<AdminUserResDto> findUserList(String token) {

        // 관리자 체크

        return null;
    }

    @Override
    public AdminUserResDto findUser(Long userId, String token) {

        // 관리자 체크
        
        return null;
    }


    /**
     * 아이디(이메일) 중복 체크
     * @param email
     * @return
     */
    @Transactional(readOnly = true)
    @Override
    public Boolean checkEmail(String email) {
        Optional< Users> user = userRepository.findByEmail(email);

        // 이미 존재하는 유저면 false
        if(user.isPresent())
            return false;

        return true;
    }

    @Override
    public void sendEmailCode(String email) {

    }

    @Override
    public Boolean checkEmailCode(CheckEmailCodeReqDto checkEmailCodeReqDto) {
        return null;
    }

    @Override
    public void addJiroCode(JiroCodeRepDto jiroCodeRepDto, String token) {

    }

//    /**
//     * 로그인 유저가 관리자 회원인지 확인합니다.
//     * @param token
//     * @return
//     */
//    public Boolean checkAdmin(String token) {
//
//    }


}
