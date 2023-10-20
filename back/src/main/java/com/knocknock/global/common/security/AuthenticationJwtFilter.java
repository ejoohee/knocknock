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


        }


//        String authorizationHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
//
//        // Header의 Authorization 값이 비어있을 때 => Jwt token 전송 X => 로그인 X
//        if(authorizationHeader == null) {
//            filterChain.doFilter(req, res);
//            return;
//        }
//
//        // Header의 Authorization 값의 접두사가 'Bearer '로 시작하지 않으면 => 잘못된 토큰
//        if(!authorizationHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(req, res);
//            return;
//        }
//
//        // 전송받은 값에서 'Bearer ' 뒷부분(Jwt token) 추출
//        String token = authorizationHeader.split(" ")[1];
//
//        // 전송받은 Jwt token이 만료되었으면 => 다음 필터 진행(인증 X)
//        if(jwtUtil.isTokenExpired(token)) {
//            filterChain.doFilter(req, res);
//            return;
//        }
//
//        // Jwt token에서 loginId 추출
//        Long loginUserNo = jwtUtil.getUserNo(token);
//
//        // loginId로 User 찾기
//        Users loginUser = userService.getLoginUserByLoginId(loginId);
//
//        // loginUser 정보로 UsernamePasswordAuthenticationToken 발급
//        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//                loginUser.getLoginId(), null, List.of(new SimpleGrantedAuthority(loginUser.getRole().name())));
//        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
//
//        // 권한 부여
//        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(req, res);
    }

    private String getAccessToken(HttpServletRequest req) {
        String headerAuth = req.getHeader(JwtHeaderUtilEnum.AUTHORIZATION.getValue());

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
