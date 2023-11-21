package com.knocknock.global.common.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtHeaderUtilEnum {

    AUTHORIZATION("Authorization 헤더 ", "Authorization"),
    GRANT_TYPE("JWT 타입 / Bearer ", "Bearer ");

    private String description;
    private String value;

}
