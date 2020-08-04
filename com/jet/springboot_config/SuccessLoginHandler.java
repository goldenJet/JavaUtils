package com.jet.springboot_config;

import java.io.IOException;

import lombok.extern.log4j.Log4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by jet.chen on 2017/6/6.
 */
@Service
@Log4j
public class SuccessLoginHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        Cookie cookie = new Cookie("name", "eric");
        cookie.setMaxAge(-1);
        httpServletResponse.addCookie(cookie);
        httpServletResponse.sendRedirect("/");

        // 登录成功后 TODO something
    }
}
