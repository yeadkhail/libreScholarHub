package com.ynm.researchpaperservice.service;

import com.ynm.researchpaperservice.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public UserDetailsServiceImpl(RestTemplate restTemplate,
                                  @Value("${user.service.url}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    @Cacheable(value = "users", key = "#username")
    @Override
    public UserDetails loadUserByUsername(String username) {
        try {
            log.debug("Attempting to load user with username: {}", username);
            // Log the exact URL you're calling
            log.debug("Calling User Management Service at: {}", userServiceUrl + "/users/email/" + username);

            ResponseEntity<UserDto> response = restTemplate.getForEntity(
                    userServiceUrl + "/users/email/" + username,
                    UserDto.class
            );

            log.debug("Response status: {}", response.getStatusCode());
            log.debug("Response body: {}", response.getBody());

            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to load user from User Management Service: {}", e.getMessage(), e);
            throw new UsernameNotFoundException("User not found", e);
        }
    }
}