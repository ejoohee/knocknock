package com.knocknock.global.common.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 인증 안됨(401 에러) 발생시 처리
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException authException) throws IOException, ServletException {
        res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "401에러 발생");
    }
}
