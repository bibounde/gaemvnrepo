package com.bibounde.gaemvnrepo.web.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class CustomDelegatingAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String MAVEN_USER_AGENT_PREFIX = "Apache-Maven";
    
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(CustomDelegatingAuthenticationEntryPoint.class);
    
    private AuthenticationEntryPoint basicAuthenticationEntryPoint;
    private AuthenticationEntryPoint loginFormAuthenticationEntryPoint;
    
    public CustomDelegatingAuthenticationEntryPoint(AuthenticationEntryPoint basicAuthenticationEntryPoint, AuthenticationEntryPoint loginFormAuthenticationEntryPoint) {
        this.basicAuthenticationEntryPoint = basicAuthenticationEntryPoint;
        this.loginFormAuthenticationEntryPoint = loginFormAuthenticationEntryPoint;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String userAgent = request.getHeader("User-Agent");
        
        if (userAgent != null && userAgent.startsWith(MAVEN_USER_AGENT_PREFIX)) {
            logger.debug("Authentication Basic for User-Agent : {}", userAgent);
            this.basicAuthenticationEntryPoint.commence(request, response, authException);
        } else {
            logger.debug("Authentication with form for User-Agent : {}", userAgent);
            this.loginFormAuthenticationEntryPoint.commence(request, response, authException);
        }
        
    }
}
