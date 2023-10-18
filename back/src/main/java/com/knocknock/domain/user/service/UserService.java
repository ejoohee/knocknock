package com.knocknock.domain.user.service;

import com.knocknock.domain.user.dto.*;

import java.util.List;

public interface UserService {

    void signUp(UserReqDto userReqDto);

    UserResDto login(LoginReqDto loginReqDto);

    Boolean checkEmail(String email);

    void logout(String accessToken);

    void sendEmailCode(String email);

    Boolean checkEmailCode(CheckEmailCodeReqDto checkEmailCodeReqDto);

    // ReissueTokenResDto reissueToken(String accessToken, String refreshToken);

    void updateUser(UpdateUserReqDto updateUserReqDto);

    void deleteUser();
    void deleteUser(String email);

    UserResDto findUser();

    List<UserResDto> findUserList();

    void updatePassword(PasswordReqDto passwordReqDto);
}
