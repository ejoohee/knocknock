package com.knocknock.global.common.security;

import com.knocknock.domain.user.dao.LogoutAccessTokenRedisRepository;
import com.knocknock.global.common.jwt.JwtHeaderUtilEnum;
import com.knocknock.global.exception.SecurityExceptionMessage;
import com.knocknock.global.exception.exception.TokenException;
import com.knocknock.global.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/*
사용자의 요청에서 Jwt Token을 추출해 
통과하면 권한을 부여하고, 
실패하면 권한부여 없이 다음 필터로 진행시킴
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class AuthenticationJwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * refresh token을 이용한 access token 재발급시 필터를 거치지 않도록 합니다.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) throws ServletException {
        return req.getRequestURI().contains("/reissue-token");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getAccessToken(req);
        log.info("get해와서 accessToken : {}", accessToken);

        if (accessToken != null && !accessToken.equals("undefined")) {

            // 로그아웃 여부 확인
            // 로그아웃한 상태면 해당 accessToken은 만료되지 않았어도 무효함
            checkLogout(accessToken);

            String email = null;
            try {
                email = jwtUtil.getLoginEmail(accessToken);
                log.info("[토큰 필터] email : {}", email);
            } catch (ExpiredJwtException e) {
                // 토큰 만료
                log.info("[토큰 필터] 토큰 만료.");
                setResponse(res, HttpStatus.UNAUTHORIZED, "만료된 토큰입니다.");
            } catch (SignatureException e) { // 체크(쓰면안대)
                log.info("[토큰 필터] 유효하지 않은 토큰");
                setResponse(res, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
            }

            log.info("[토큰 필터] 토큰 검사 완료!");

            if(email == null) {
                log.error("[토큰 필터] 유저 정보를 반환하지 못했습니다.");
                throw new TokenException("토큰으로 유저 정보를 반환하지 못했습니다.");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 액세스 토큰 생성 시 사용된 이메일 아이디와 현재 이메일 아이디가 일치하는지 확인
            equalsUsernameFromTokenAndUserDetails(userDetails.getUsername(), email);
            // 액세스 토큰의 유효성 검증
            validateAccessToken(accessToken, userDetails);
            // securityContextHolder에 인증된 회원의 정보를 저장
            processSecurity(req, userDetails);

            log.info("[토큰 유효성 검사 완전완전 완료~~] user : {}", userDetails.getUsername());
        }

        // 다음 순서 필터로 넘어가기
        log.info("[다음 순서 필터로 넘어가]");
        filterChain.doFilter(req, res);
    }

    private String getAccessToken(HttpServletRequest req) {

//        Enumeration<String> headerNames = req.getHeaderNames();
//
//        log.info("headerNames 뽑아와요");
//        while (headerNames.hasMoreElements()) {
//
//            String headerName = headerNames.nextElement();
//            String headerValue = req.getHeader(headerName);
//            log.info("{} : {}", headerName ,headerValue);
//
//        }
//        log.info("headerNames 다뽑았어요");

        String headerAuth = req.getHeader(JwtHeaderUtilEnum.AUTHORIZATION.getValue()); // 포스트맨용
//        String headerAuth = req.getHeader("accessToken"); // 스웨거용

        log.info("[header] {}", headerAuth);

        if(StringUtils.hasText(headerAuth) && headerAuth.startsWith(JwtHeaderUtilEnum.GRANT_TYPE.getValue())) {
            return headerAuth.substring(JwtHeaderUtilEnum.GRANT_TYPE.getValue().length());
        }

        return null;
    }

    private void checkLogout(String accessToken) {
        if(logoutAccessTokenRedisRepository.existsById(accessToken)) {
            throw new IllegalArgumentException("이미 로그아웃된 회원입니다.");
        }
    }

    /**
     * 토큰 관련 에러 처리
     */
    private void setResponse(HttpServletResponse res, HttpStatus httpStatus, String errorMsg) throws RuntimeException, IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setStatus(httpStatus.value());
        res.getWriter().print(errorMsg);
    }

    private void equalsUsernameFromTokenAndUserDetails(String userDetailsUsername, String tokenUsername) {
        if (!userDetailsUsername.equals(tokenUsername)) {
            throw new IllegalArgumentException(SecurityExceptionMessage.MISMATCH_TOKEN_EMAIL.getMsg());
        }
    }

    private void validateAccessToken(String accessToken, UserDetails userDetails) {
        if (!jwtUtil.validateToken(accessToken, userDetails)) {
            throw new IllegalArgumentException(SecurityExceptionMessage.INVALID_TOKEN.getMsg());
        }
    }

    private void processSecurity(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

}
