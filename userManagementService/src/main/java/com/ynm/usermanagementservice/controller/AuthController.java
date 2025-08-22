package com.ynm.usermanagementservice.controller;

import com.ynm.usermanagementservice.repository.RefreshTokenRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ynm.usermanagementservice.dao.LoginResponse;
import com.ynm.usermanagementservice.dao.LoginRequest;
import com.ynm.usermanagementservice.dao.RegisterRequest;
import com.ynm.usermanagementservice.service.JWTServiceImpl;
import com.ynm.usermanagementservice.model.User;
import com.ynm.usermanagementservice.service.UserService;
import com.ynm.usermanagementservice.service.UserDetailsServiceImpl;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import com.ynm.usermanagementservice.model.RefreshToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@RestController
@RequestMapping("/api/auth/")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTServiceImpl jwtService;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @GetMapping("")
    public ResponseEntity<Object> getAuthUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated() ||
                    authentication.getPrincipal().equals("anonymousUser")) {
                System.err.println(authentication.getPrincipal());
                return ResponseEntity.status(401).body("Unauthorized");
            }

            User userDetails = (User) authentication.getPrincipal();

            System.err.println(authentication.getPrincipal());

            // Fetch from DB for full info
            var user = userService.getUserByEmail(userDetails.getUsername());
            return ResponseEntity.ok(Map.of(
                    "email", user.getEmail(),
                    "fullName", user.getFullName(),
                    "role", user.getRole().getName()

            ));
        } catch (Exception e) {
            log.error("Failed to fetch authenticated user: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("health")
    public ResponseEntity<String> healthCheck() {
        try {
            log.info("Health check requested");
            return ResponseEntity.ok("Auth service is running");
        } catch (Exception e) {
            log.error("Health check failed: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Health check failed: " + e.getMessage());
        }
    }

    @PostMapping("login")
    public ResponseEntity<?> loginHandler(@RequestBody LoginRequest loginRequest) {
        try {
            log.info("Attempting login for email: {}", loginRequest.getEmail());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = (User) authentication.getPrincipal(); // Your User implements UserDetails
            String accessToken = jwtService.generateToken(user);
            UUID uuid = UUID.randomUUID();
            String uuidString = uuid.toString();
            String refreshToken = jwtService.generateRefreshToken(Map.of("uuid", uuidString), user);

            // Save refresh token to DB
            RefreshToken refreshTokenEntity = new RefreshToken();
            refreshTokenEntity.setToken(refreshToken);
            refreshTokenEntity.setUserId(user.getId());
            refreshTokenRepository.save(refreshTokenEntity);

            log.info("Login successful for email: {}", loginRequest.getEmail());
            return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));

        } catch (Exception e) {
            log.error("Login failed for email: {} - Error: {}", loginRequest.getEmail(), e.getMessage(), e);
            return ResponseEntity.status(401).body("Authentication failed: " + e.getMessage());
        }
    }

    @PostMapping("register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            log.info("Attempting registration for email: {}", registerRequest.getEmail());

            String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());
            boolean isEmailRegistered = userService.isEmailRegistered(registerRequest.getEmail());
            if(isEmailRegistered) return ResponseEntity.badRequest().body("Email is already registered");

            userService.save(registerRequest.getFullName(), registerRequest.getEmail(), hashedPassword);

            log.info("Registration successful for email: {}", registerRequest.getEmail());
            return ResponseEntity.ok("User registered successfully");

        } catch (Exception e) {
            log.error("Registration failed for email: {} - Error: {}", registerRequest.getEmail(), e.getMessage(), e);
            return ResponseEntity.status(500).body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("logout")
    public ResponseEntity<String> logout(@RequestBody Map<String, String> request) {
        try {
            log.info("Processing logout request");

            // Get access token from request body
            String accessToken = request.get("accessToken");
            if (accessToken == null || accessToken.isEmpty()) {
                return ResponseEntity.badRequest().body("Access token is required");
            }

            // Verify token and extract user email
            String userEmail = jwtService.extractUsername(accessToken);
            if (userEmail == null) {
                return ResponseEntity.status(401).body("Invalid access token");
            }

            // Check if token is valid
            boolean isTokenValid = jwtService.validateToken(accessToken);
            if (!isTokenValid) {
                return ResponseEntity.status(401).body("Invalid or expired access token");
            }

            // Delete refresh token from database for this user
            boolean deleted = userService.deleteRefreshTokenForUser(userEmail);

            // Clear the security context
            SecurityContextHolder.clearContext();

            log.info("Logout successful for user: {}", userEmail);
            return ResponseEntity.ok("Logout successful");
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Logout failed: " + e.getMessage());
        }
    }

// In AuthController.java

    @PostMapping("refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Refresh token is required");
        }

        // Check if refresh token exists in DB
        Optional<RefreshToken> tokenEntity = refreshTokenRepository.findByToken(refreshToken);
        if (tokenEntity.isEmpty()) {
            return ResponseEntity.status(401).body("Refresh token not found");
        }

        if (!jwtService.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(401).body("Invalid or expired refresh token");
        }

        String username = jwtService.getUsernameFromRefreshToken(refreshToken);
        if (username == null) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }

        UserDetails userDetails = userService.getUserByEmail(username);
        String newAccessToken = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

}



