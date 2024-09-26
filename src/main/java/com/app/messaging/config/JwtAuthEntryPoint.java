package com.app.messaging.config;

import java.io.IOException;
import java.rmi.ServerException;

import org.hibernate.annotations.Comment;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint{

    @Override
    public void commence (HttpServletRequest request, HttpServletResponse reponse, AuthenticationException authException) throws IOException, ServerException{
        reponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
    
}
