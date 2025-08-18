package com.ynm.usermanagementservice.web.dto;

import lombok.Data;

import java.util.Set;

public class AuthDtos {
    @Data
    public static class RegisterRequest {
        private String username;
        private String password;
        private Set<String> roles; // optional; default ROLE_USER
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    public static class TokenResponse {
        private String token;
        private String tokenType = "Bearer";
        private long expiresIn;

        public TokenResponse(String token, long expiresIn) {
            this.token = token;
            this.expiresIn = expiresIn;
        }
    }
}
