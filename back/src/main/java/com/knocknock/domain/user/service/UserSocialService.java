package com.knocknock.domain.user.service;

import com.knocknock.domain.user.dto.request.UpdateAddressReqDto;
import com.knocknock.domain.user.dto.response.SocialLoginResDto;
import com.knocknock.domain.user.dto.response.UpdateAddressResDto;

public interface UserSocialService {
    SocialLoginResDto socialLogin(String code);

    UpdateAddressResDto updateAddress(UpdateAddressReqDto updateAddressReqDto, String token);
}
