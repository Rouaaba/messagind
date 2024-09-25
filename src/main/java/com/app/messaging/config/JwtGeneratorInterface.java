package com.app.messaging.config;

import java.util.Map;

import org.springframework.security.core.Authentication;

public interface JwtGeneratorInterface {
    Map<String, String> generateToken(Authentication authentication);
}
