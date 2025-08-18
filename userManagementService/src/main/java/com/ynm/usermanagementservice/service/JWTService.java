package com.ynm.usermanagementservice.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JWTService {
    String generateToken(UserDetails userDetails);
    String generateRefreshToken(Map<String,Object> extraClaims , UserDetails userDetails);
    String extractUserName(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
    boolean isTokenExpired(String token);
    void deleteRefreshToken(Long userId);
    void saveRefreshToken(Long userId, String refreshToken);
    boolean validateRefreshToken(Long userId, String refreshToken);
}
