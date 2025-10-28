package com.example.springboot.service;

import com.example.springboot.dto.UserTransferDto; // 1. Use the new DTO
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserServiceClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceClient.class);
    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public UserServiceClient(RestTemplate restTemplate,
                             @Value("${user.service.url}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    public String getEmailByUserId(Long id) {
        try {
            String url = userServiceUrl + "/users/id/" + id;
            LOGGER.info("Calling User service: {}", url);

            UserTransferDto user = restTemplate.getForObject(url, UserTransferDto.class);

            if (user != null && user.getEmail() != null) {
                return user.getEmail();
            } else {
                LOGGER.warn("User data was null or email was missing for ID: {}", id);
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("Failed to fetch user email for ID {}: {}", id, e.getMessage());
            return null;
        }
    }
}