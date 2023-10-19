package com.knocknock.global.common.security;

import com.knocknock.domain.user.dao.LogoutAccessTokenRedisRepository;
import com.knocknock.domain.user.domain.Users;
import com.knocknock.domain.user.service.UserService;
import com.knocknock.global.common.jwt.JwtHeaderUtilEnum;
import com.knocknock.global.common.security.UserDetailsServiceImpl;
import com.knocknock.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/*
사용자의 요청에서 Jwt Token을 추출해 
통과하면 권한을 부여하고, 
실패하면 권한부여 없이 다음 필터로 진행시킴
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

//    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final LogoutAccessTokenRedisRepository

    /**
     * refresh token을 이용한 access token 재발급시 필터를 거치지 않도록 합니다.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) throws ServletException {
        return req.getRequestURI().contains("/reissue-token");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = req.getHeader(HttpHeaders.AUTHORIZATION);

        // Header의 Authorization 값이 비어있을 때 => Jwt token 전송 X => 로그인 X
        if(authorizationHeader == null) {
            filterChain.doFilter(req, res);
            return;
        }

        // Header의 Authorization 값의 접두사가 'Bearer '로 시작하지 않으면 => 잘못된 토큰
        if(!authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(req, res);
            return;
        }

        // 전송받은 값에서 'Bearer ' 뒷부분(Jwt token) 추출
        String token = authorizationHeader.split(" ")[1];

        // 전송받은 Jwt token이 만료되었으면 => 다음 필터 진행(인증 X)
        if(jwtUtil.isTokenExpired(token)) {
            filterChain.doFilter(req, res);
            return;
        }

        // Jwt token에서 loginId 추출
        Long loginUserNo = jwtUtil.getUserNo(token);

        // loginId로 User 찾기
        Users loginUser = userService.getLoginUserByLoginId(loginId);

        // loginUser 정보로 UsernamePasswordAuthenticationToken 발급
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginUser.getLoginId(), null, List.of(new SimpleGrantedAuthority(loginUser.getRole().name())));
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

        // 권한 부여
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(req, res);
    }

    private String getAccessToken(HttpServletRequest req) {
        String headerAuth = req.getHeader(JwtHeaderUtilEnum.AUTHORIZATION.getValue());

        if(StringUtils.hasText(headerAuth) && headerAuth.startsWith(JwtHeaderUtilEnum.GRANT_TYPE.getValue())) {
            return headerAuth.substring(JwtHeaderUtilEnum.GRANT_TYPE.getValue().length());
        }

        return null;
    }


}
