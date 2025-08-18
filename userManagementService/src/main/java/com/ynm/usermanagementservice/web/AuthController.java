package com.ynm.usermanagementservice.web;

import com.ynm.usermanagementservice.model.AppUser;
import com.ynm.usermanagementservice.security.JwtService;
import com.ynm.usermanagementservice.service.UserService;
import com.ynm.usermanagementservice.web.dto.AuthDtos.LoginRequest;
import com.ynm.usermanagementservice.web.dto.AuthDtos.RegisterRequest;
import com.ynm.usermanagementservice.web.dto.AuthDtos.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    @org.springframework.beans.factory.annotation.Value("${security.jwt.expiration-seconds:3600}")
    private long expirationSeconds;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            AppUser user = userService.register(request.getUsername(), request.getPassword(), request.getRoles());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "roles", user.roleSet()
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        Set<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        Map<String, Object> claims = new HashMap<>();
        String token = jwtService.generateToken(request.getUsername(), roles, claims);
        // default expiration is in configuration; expose for clients
        return ResponseEntity.ok(new TokenResponse(token, expirationSeconds));
    }
}
