package com.knocknock.global.common.jwt;

import com.knocknock.domain.user.domain.Users;
import com.knocknock.domain.user.service.UserService;
import com.knocknock.global.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final String secretKey;
    private final JwtTokenUtil jwtTokenUtil;

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
        if(JwtTokenUtil.isExpired(token, secretKey)) {
            filterChain.doFilter(req, res);
            return;
        }

        // Jwt token에서 loginId 추출
        String loginId = JwtTokenUtil.getLoginId(token, secretKey);

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
}
