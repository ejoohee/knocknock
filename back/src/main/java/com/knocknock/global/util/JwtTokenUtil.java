package com.knocknock.global.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

// Jwt Token 방식을 사용할 때 필요한 기능들을 정리해놓은 클래스
// 새로운 Jwt Token 발급, Jwt Token의 Claim에서 "loginId" 꺼내기, 만료기간 체크 기능 수행
public class JwtTokenUtil {

    /**
     * JWT Token을 발급합니다.
     * @param loginId
     * @param key
     * @param expireTimeMs
     * @return
     */
    public static String createToken(String loginId, String key, long expireTimeMs) {
        // Claim = Jwt Token에 들어갈 정보
        Claims claims = Jwts.claims();
        claims.put("loginId", loginId); // loginId를 넣어주어 나중에 꺼내 쓸 수 있음
    
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

//    public static String getLoginId()
}
