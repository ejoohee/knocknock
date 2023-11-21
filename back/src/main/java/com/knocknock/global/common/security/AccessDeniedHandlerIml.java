package com.knocknock.global.common.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 권한 없음(403 에러) 발생시 처리
 */
@Component
public class AccessDeniedHandlerIml implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        res.sendError(HttpServletResponse.SC_FORBIDDEN, "인증은 됐으나 권한이 없어용");
    }
}
