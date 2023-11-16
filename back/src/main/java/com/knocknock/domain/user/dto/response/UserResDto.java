package com.knocknock.domain.user.dto.response;

import com.knocknock.domain.user.domain.Users;
import lombok.Builder;
import lombok.Data;

/**
 * 로그인 유저가 본인의 정보를 조회할 때 일반적으로 쓰이며,
 * userId, password, userType 등을 제외한 기본적인 정보만 조회됩니다.
 */
@Data
@Builder
public class UserResDto {

    private String nickname;
    private String email;
    private String giroCode;
    private String address;
    private String stationName;

    public static UserResDto entityToDto(Users user) {
        return UserResDto.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .giroCode(user.getGiroCode())
                .address(user.getAddress())
                .stationName(user.getAirStation())
                .build();
    }
}
