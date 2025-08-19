package com.ynm.usermanagementservice.controller;

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
import java.util.UUID;

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
            String accessToken = jwtService.generateToken((UserDetails) authentication.getPrincipal());
            UUID uuid = UUID.randomUUID();
            String uuidString = uuid.toString();
            String refreshToken = jwtService.generateRefreshToken(Map.of("uuid", uuidString), (UserDetails) authentication.getPrincipal());

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
}
