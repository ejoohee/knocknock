package com.knocknock.domain.user.service;

import com.knocknock.domain.user.dao.UserRepository;
import com.knocknock.domain.user.domain.Users;
import com.knocknock.domain.user.dto.*;
import com.knocknock.global.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final JwtTokenUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JavaMailSender javaMailSender;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void signUp(UserReqDto userReqDto) {

    }

    @Override
    public UserResDto login(LoginReqDto loginReqDto) {
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
    public void logout(String accessToken) {
        String email =

    }

    @Override
    public void sendEmailCode(String email) {

    }

    @Override
    public Boolean checkEmailCode(CheckEmailCodeReqDto checkEmailCodeReqDto) {
        return null;
    }

    @Override
    public void updateUser(UpdateUserReqDto updateUserReqDto) {

    }

    @Override
    public void deleteUser() {

    }

    @Override
    public UserResDto findUser() {
        return null;
    }


    /**
     * 현재 로그인한 회원의 아이디(이메일)을 반환
     */
    private String getLoginEmail() {

    }

    /**
     * 현재 로그인한 회원의 회원번호(기본키)를 반환
     */
    private Long getUserNo() {

    }

}
