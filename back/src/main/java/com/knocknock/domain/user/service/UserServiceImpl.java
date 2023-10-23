package com.knocknock.domain.user.service;

import com.knocknock.domain.user.dao.LogoutAccessTokenRedisRepository;
import com.knocknock.domain.user.dao.RefreshTokenRedisRepository;
import com.knocknock.domain.user.dao.UserRepository;
import com.knocknock.domain.user.domain.LogoutAccessToken;
import com.knocknock.domain.user.domain.RefreshToken;
import com.knocknock.domain.user.domain.Users;
import com.knocknock.domain.user.dto.password.FindPasswordReqDto;
import com.knocknock.domain.user.dto.password.PasswordReqDto;
import com.knocknock.domain.user.dto.password.UpdatePasswordReqDto;
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
        String email = userReqDto.getEmail();
        log.info("[유저 회원가입] 회원가입 요청. email : {}", email);

        // 중복 이메일 체크
        if(!checkEmail(email)) {
            log.error("[유저 회원가입] 이미 존재하는 이메일입니다.");
            throw new UserNotFoundException(UserExceptionMessage.EMAIL_DUPLICATED.getMessage());
        }

        // 회원가입 정보 유효성 확인
        if(!checkSignupInfo(userReqDto)) {
            log.error("[유저 회원가입] 회원가입 정보 유효성 불일치.");
            throw new IllegalArgumentException(UserExceptionMessage.SIGN_UP_NOT_VALID.getMessage());
        }

        log.info("[유저 회원가입] 이메일 인증 코드 발송 요청.");
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        // 이메일 인증 여부 확인
        String checkResult = (String) valueOperations.get(userReqDto.getEmail());
        if(checkResult == null || !checkResult.equals("인증완료"))
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

        log.info("[유저 로그인] 로그인 요청. email : {}", email);

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

    /**
     * 로그인 유저를 반환
     */
    private Users getLoginUser(String token) {
        String email = jwtUtil.getLoginEmail(token);

        Users loginUser = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[UserService] 로그인 유저를 찾을 수 없습니다.");
                    return new UserNotFoundException(UserExceptionMessage.USER_NOT_FOUND.getMessage());
                });

        return loginUser;
    }

    @Override
    public void logout(String token) {
        log.info("[로그아웃] 로그아웃 요청! 유저 조회 실시.");

        // 로그아웃 여부를 redis에 넣어서 accessToken이 유효한지 체크
        String email = jwtUtil.getLoginEmail(token);
        log.info("[로그아웃] 로그아웃 요청 email : {}", email);

        long remainMilliSeconds = jwtUtil.getRemainMilliSeconds(token);
        refreshTokenRepository.deleteById(email);
        logoutAccessTokenRepository.save(LogoutAccessToken.builder()
                .email(email)
                .accessToken(token)
                .expiration(remainMilliSeconds / 1000)
                .build());

        log.info("[로그아웃] 로그아웃 처리 완료!");
    }

    /**
     * 비밀번호 찾기를 요청하면
     * 이메일로 임시 비밀번호가 발급됩니다.
     * @param findPasswordReqDto
     */
    @Override
    public void findPassword(FindPasswordReqDto findPasswordReqDto) {
        String email = findPasswordReqDto.getEmail();
        log.info("[비밀번호 찾기] 찾기 요청. email : {}", email);

        Users findUser = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[비밀번호 찾기] 존재하지 않는 회원입니다.");
                    return new UserNotFoundException(UserExceptionMessage.USER_NOT_FOUND.getMessage());
                });

        // 유저 닉네임 일치 확인
        if(!findUser.getNickname().equals(findPasswordReqDto.getNickname())) {
            log.error("[비밀번호 찾기] 정보가 일치하지 않습니다.");
            throw new UserNotFoundException(UserExceptionMessage.INFO_NOT_VALID.getMessage());
        }

        // 일치하면 메일 발송 요청 ---> 프론트

        // 코드 일치 확인할 필요없음
        // 받은 비번으로 지들이 로그인 할거니까
    }

    /**
     * 임시 비밀번호를 발급합니다.
     */
    @Override
    public void updateTempPassword(String email, String tempPassword) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("[임시 비밀번호 발급] 존재하지 않는 회원입니다.");
                    return new UserNotFoundException(UserExceptionMessage.USER_NOT_FOUND.getMessage());
                });

        // 임시 패스워드 암호화
        String encodingPassword = passwordEncoder.encode(tempPassword);
        user.updatePassword(encodingPassword);
        userRepository.save(user);
    }

    @Override
    public Boolean checkPassword(PasswordReqDto passwordReqDto, String token) {
        return null;
    }

    @Override
    public void updatePassword(UpdatePasswordReqDto updatePasswordRepDto, String token) {

        // 유저
        Users loginUser = getLoginUser(token);
        log.info("[비밀번호 변경] 변경 요청. 로그인 유저 : {}", loginUser.getEmail());

        String originPassword = loginUser.getPassword();
        // 패스워드 일치 확인
        if(!passwordEncoder.matches(updatePasswordRepDto.getPassword(), originPassword)) {
            log.error("[비밀번호 변경] 비밀번호 불일치! 변경 불가.");
            throw new UserNotFoundException(UserExceptionMessage.LOGIN_PASSWORD_ERROR.getMessage());
        }

        // 패스워드 일치하면 변경 가능
        // 이전이랑 달라야함 ---> 프론트에서 거를건가?
        if(passwordEncoder.matches(updatePasswordRepDto.getNewPassword(), originPassword)) {
            log.error("[비밀번호 변경] 이전 비밀번호와 다른 비밀번호를 입력해야 합니다.");
            throw new UserNotFoundException(UserExceptionMessage.UPDATE_SAME_PASSWORD_ERROR.getMessage());
        }

        // 확인한 비밀번호와 일치하지 않음
        if(!updatePasswordRepDto.getNewPassword().equals(updatePasswordRepDto.getNewPasswordCheck())) {
            log.error("[비밀번호 변경] 입력한 비밀번호가 일치하지 않습니다.");
            throw new UserNotFoundException(UserExceptionMessage.UPDATE_PASSWORD_ERROR.getMessage());
        }

        log.info("[비밀번호 변경] 패스워드 변경 완료.");

        // 패스워드 암호화
        String encodingPassword = passwordEncoder.encode(updatePasswordRepDto.getNewPassword());
        loginUser.updatePassword(encodingPassword);
        userRepository.save(loginUser);
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
    public void addJiroCode(JiroCodeReqDto jiroCodeRepDto, String token) {

    }


//    @Override
//    public Boolean checkEmailCode(EmailCodeReqDto checkEmailCodeReqDto) {
//        return null;
//    }


//    /**
//     * 로그인 유저가 관리자 회원인지 확인합니다.
//     * @param token
//     * @return
//     */
//    public Boolean checkAdmin(String token) {
//
//    }


}
