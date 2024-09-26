package com.app.messaging.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

import com.app.messaging.domain.AuthResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtGenerator jwtGenerator;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Generate JWT token
        String token = jwtGenerator.generateToken(authentication);

        // Extract the role
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER"); // Default to "ROLE_USER" if no authority found

        // Prepare the response in JSON format
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);

        // Construct a JSON response with token and role
        String jsonResponse = String.format("{\"accessToken\": \"%s\", \"tokenType\": \"Bearer\", \"role\": \"%s\"}", token, role);

        // Write the JSON response
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
