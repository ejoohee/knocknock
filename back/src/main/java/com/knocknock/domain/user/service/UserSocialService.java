package com.knocknock.domain.user.service;

import com.knocknock.domain.user.dto.response.SocialLoginResDto;

public interface UserSocialService {
    SocialLoginResDto socialLogin(String code);
}
