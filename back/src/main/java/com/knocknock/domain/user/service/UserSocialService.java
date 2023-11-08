package com.knocknock.domain.user.service;

import com.knocknock.domain.user.dto.request.CheckGoogleReqDto;
import com.knocknock.domain.user.dto.request.GoogleLoginReqDto;
import com.knocknock.domain.user.dto.request.UpdateAddressReqDto;
import com.knocknock.domain.user.dto.response.SocialLoginResDto;
import com.knocknock.domain.user.dto.response.UpdateAddressResDto;

public interface UserSocialService {
    SocialLoginResDto socialLogin(String code);

    void checkSocialLogin(CheckGoogleReqDto checkGoogleReqDto);

    SocialLoginResDto socialFrontLogin(GoogleLoginReqDto googleLoginReqDto);

    UpdateAddressResDto updateAddress(UpdateAddressReqDto updateAddressReqDto, String token);
}
