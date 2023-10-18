package com.knocknock.global.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
// Jwt Token 방식을 사용할 때 필요한 기능들을 정리해놓은 클래스
// 새로운 Jwt Token 발급, Jwt Token의 Claim에서 "loginId" 꺼내기, 만료기간 체크 기능 수행
public class JwtTokenUtil {

    @Value("${jwt.secret_key}")
    private String SECRET_KEY;

    @Value("${jwt.access_expiration_ms}")
    private long accessExpirationMs;

    @Value("${jwt.refresh_expiration_ms}")
    private long refreshExpirationMs;

    @Value("${jwt.issuer}")
    private String issuer;


    /**
     * JWT Token을 발급합니다.
     * @param email
     * @param expireTimeMs
     * @return
     */
    private String createToken(String email, long expireTimeMs) {
        // Claim = Jwt Token에 들어갈 정보
        Claims claims = Jwts.claims();
        claims.put("email", email); // loginEmail을 넣어주어 나중에 꺼내 쓸 수 있음
    
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))
                .signWith(getSigningKey(SECRET_KEY), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * SecretKey를 사용해 Token parsing
     * @param token
     * @return
     */
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(SECRET_KEY))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Claims에서 loginEmail 추출
     */
    public String getLoginEmail(String token) {
        return extractClaims(token).get("email").toString();
    }

    /**
     * 발급된 토큰이 만료되었는지 체크
     * @param token
     * @param secretKey
     * @return
     */
    public static boolean isExpired(String token, String secretKey) {
        Date expiredDate = extractClaims(token, secretKey).getExpiration();
        
        // 토큰의 만료 날짜가 현재보다 이전인지 체크
        return expiredDate.before(new Date());
    }



    private Key getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
