package com.knocknock.domain.user.dto.response;

import com.knocknock.domain.user.domain.Users;
import lombok.Builder;
import lombok.Data;

/**
 * 관리자 회원이 회원 검색할 때 반환하는 DTO로
 * 회원의 모든 정보를 반환합니다.
 */
@Data
@Builder
public class AdminUserResDto {

    private Long userId;
    private String email;
    private String password;
    private String nickname;
    private String userType;
    private String giroCode;
    private String address;

    public static AdminUserResDto entityToDto(Users user) {
        return AdminUserResDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .userType(user.getUserType().getValue())
                .giroCode(user.getGiroCode())
                .address(user.getAddress())
                .build();
    }

}
