package com.knocknock.global.util;

import com.knocknock.global.common.security.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Component
// Jwt Token 방식을 사용할 때 필요한 기능들을 정리해놓은 클래스
// 새로운 Jwt Token 발급, Jwt Token의 Claim에서 "loginId" 꺼내기, 만료기간 체크 기능 수행
public class JwtUtil {

    @Value("${jwt.secret_key}")
    private String SECRET_KEY;

    @Value("${jwt.access_expiration_ms}")
    private long accessExpirationMs;

    @Value("${jwt.refresh_expiration_ms}")
    private long refreshExpirationMs;

    @Value("${jwt.issuer}")
    private String issuer;


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
    
    public String generateAccessToken(String email) {
        return createToken(email, accessExpirationMs); // 확인해바야함
    }

    public String generateRefreshToken(String email) {
        return createToken(email, refreshExpirationMs);
    }

    /**
     * 발급된 토큰이 만료되었는지 체크
     * @param token
     * @return
     */
    public Boolean isTokenExpired(String token) {
        Date expiredDate = extractClaims(token).getExpiration();

        // 토큰의 만료 날짜가 현재보다 이전인지 체크
        return expiredDate.before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        String email = getLoginEmail(token);

        return email.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    /**
     * 토큰 남은 시간 반환
     * @param token
     * @return
     */
    public long getRemainMilliSeconds(String token) {
        Date expiration = extractClaims(token).getExpiration();
        Date now = new Date();
        
        return expiration.getTime() - now.getTime();
    }

    /**
     * Claims에서 loginEmail 추출
     */
    public String getLoginEmail(String token) {
        return extractClaims(token).get("email").toString();
    }



//여기부터 checkAdmin까지 테스트해봐야함
    private UserDetailsImpl getPrincipal() {
        log.info("[getPrincipal()메서드 실행]");
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Authentication authentication = Objects.requireNonNull(SecurityContextHolder
                .getContext().getAuthentication());


        log.info("authentication : {}", authentication);
        log.info("principal : {}", authentication.getPrincipal());



        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();

        return principal;
    }
    
    /**
     * userId 추출(기본키)
     */
    public Long getUserNo() {
        return getPrincipal().getUserId();
    }

    /**
     * 관리자 유저인지 체크
     */
    public Boolean checkAdmin() {
        String userType = getPrincipal().getAuthority();
        if(userType.equals("ROLE_ADMIN"))
            return true;

        return false;
    }

    private Key getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
