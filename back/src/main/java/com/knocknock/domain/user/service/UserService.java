package com.knocknock.domain.user.service;

import com.knocknock.domain.user.dto.password.FindPasswordReqDto;
import com.knocknock.domain.user.dto.password.PasswordReqDto;
import com.knocknock.domain.user.dto.password.UpdatePasswordReqDto;
import com.knocknock.domain.user.dto.request.*;
import com.knocknock.domain.user.dto.request.UserSearchCondition;
import com.knocknock.domain.user.dto.response.*;

import java.util.List;

public interface UserService {

    void signUp(UserReqDto userReqDto); // 회원가입
//    Boolean checkEmail(String email); // 이메일 중복검사
//    void sendEmailCode(String email); // 이메일 인증코드 발신 -> emailService
//    Boolean checkEmailCode(EmailCodeReqDto emailCodeReqDto); // 이메일 인증코드 유효 검사
    

    void addGiroCode(GiroCodeReqDto giroCodeRepDto, String token); // 지로 코드 등록

    LoginResDto login(LoginReqDto loginReqDto);
    void logout(String token);

    Boolean findPassword(FindPasswordReqDto findPasswordReqDto); // 비밀번호 찾아 이메일 전송하기
    void updateTempPassword(String email, String tempPassword);
    Boolean checkPassword(PasswordReqDto reqDto, String token);  // 서비스전 비밀번호 체크
    void updatePassword(UpdatePasswordReqDto updatePasswordRepDto, String token); // 비밀번호 변경


    UserResDto updateUser(UpdateUserReqDto updateUserReqDto, String token); // 내정보 수정
    void withdraw(String token); // 회원 탈퇴

    UserResDto findMyInfo(String token); // 내정보 조회

    // 토큰 재발급
//    ReissueTokenResDto reissueToken(String accessToken, String refreshToken);
    ReissueTokenResDto reissueToken(String refreshToken);

     // 관리자
    void deleteUser(Long userId, String token); // 회원 강제탈퇴
    List<AdminUserResDto> findUserList(String token); // 회원 목록 조회
    AdminUserResDto findUser(Long userId, String token); // 회원 조회
    List<AdminUserResDto> findUserByCondition(UserSearchCondition condition, String token); // 회원 검색
    List<FindPowerUsageHouseAvgResDto> findPowerUsageHouseAvgList(String metro, String city, Integer year, Integer month);
}
