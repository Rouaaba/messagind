package com.app.messaging.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
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

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(authentication);

        // Set response type to JSON
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);

        // Create a JSON object for the redirect URL
        String json = "{\"redirectUrl\": \"" + (targetUrl.equals("/admin/dashboard") ? "Admin Dashboard" : "User Dashboard") + "\"}";

        // Write the JSON response
        response.getWriter().write(json);
        response.getWriter().flush();
    }


    private String determineTargetUrl(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return "/admin/dashboard"; // Redirect to admin dashboard
        } else {
            return "/user/dashboard"; // Redirect to user home or other default page
        }
    }

}
