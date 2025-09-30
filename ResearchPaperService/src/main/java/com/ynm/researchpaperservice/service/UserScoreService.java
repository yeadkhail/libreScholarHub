package com.ynm.researchpaperservice.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserScoreService {

    private final RestTemplate restTemplate;

    private final String userServiceUrl;

    public UserScoreService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = "http://localhost:8090/";
    }
    public void syncScore(Long userId, Float newUpdate, Float lastUpdate) {
        try {
            String url = userServiceUrl + "/users/syncScore";
            log.debug("Syncing user score at: {}", url);

            // Prepare body
            Map<String, Object> scorePayload = new HashMap<>();
            scorePayload.put("userId", userId);
            scorePayload.put("lastUpdate", lastUpdate);
            scorePayload.put("newUpdate", newUpdate);

            // Extract JWT token from request
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                String bearerToken = attrs.getRequest().getHeader("Authorization");
                if (bearerToken != null && !bearerToken.isEmpty()) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Authorization", bearerToken);
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    HttpEntity<Map<String, Object>> entity =
                            new HttpEntity<>(scorePayload, headers);
                    System.out.println("Syncing user score at: " + url);
                    ResponseEntity<Void> response = restTemplate.exchange(
                            url,
                            HttpMethod.PUT,
                            entity,
                            Void.class
                    );

                    log.debug("User Service sync response status: {}", response.getStatusCode());
                } else {
                    log.warn("No Authorization header found; skipping sync");
                }
            } else {
                log.warn("No request attributes available; skipping token propagation");
            }

        } catch (Exception e) {
            log.error("Failed to sync user score: {}", e.getMessage(), e);
        }
    }

    public Float getUserScoreByEmail(String email) {
        try {
            // URL points to the endpoint in User Service that fetches score by email
            String url = userServiceUrl + "/users/email/" + email + "/score";
            log.debug("Fetching user score for email {} from: {}", email, url);

            // Extract JWT token from current request
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpHeaders headers = new HttpHeaders();

            if (attrs != null) {
                String bearerToken = attrs.getRequest().getHeader("Authorization");
                if (bearerToken != null && !bearerToken.isEmpty()) {
                    headers.set("Authorization", bearerToken);
                }
            }

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            System.out.println("Syncing user score at: " + url);
            // Make GET request to User Service
            ResponseEntity<Float> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Float.class
            );

            log.debug("Received user score for {}: {}", email, response.getBody());
            return response.getBody();

        } catch (Exception e) {
            log.error("Failed to fetch user score for {}: {}", email, e.getMessage(), e);
            return null;
        }
    }
    public Long getUserIdByEmail(String email){
        try {
            // URL points to the endpoint in User Service that fetches score by email
            String url = userServiceUrl + "/users/email/" + email + "/id";
            log.debug("Fetching user id for email {} from: {}", email, url);

            // Extract JWT token from current request
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpHeaders headers = new HttpHeaders();

            if (attrs != null) {
                String bearerToken = attrs.getRequest().getHeader("Authorization");
                if (bearerToken != null && !bearerToken.isEmpty()) {
                    headers.set("Authorization", bearerToken);
                }
            }

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            System.out.println("Syncing user score at: " + url);

            // Make GET request to User Service
            ResponseEntity<Long> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Long.class
            );

            log.debug("Received user id for {}: {}", email, response.getBody());
            return response.getBody();

        } catch (Exception e) {
            log.error("Failed to fetch user score for {}: {}", email, e.getMessage(), e);
            return null;
        }
    }

}
