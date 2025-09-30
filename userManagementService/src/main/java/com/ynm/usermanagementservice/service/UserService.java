package com.ynm.usermanagementservice.service;

import com.ynm.usermanagementservice.dto.UserDto;
import com.ynm.usermanagementservice.dto.UserTransferDto;
import com.ynm.usermanagementservice.model.Role;
import com.ynm.usermanagementservice.model.User;
import com.ynm.usermanagementservice.repository.RefreshTokenRepository;
import com.ynm.usermanagementservice.repository.RoleRepository;
import com.ynm.usermanagementservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RestTemplate restTemplate;
    private final String searchServiceUrl;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       RestTemplate restTemplate,
                       @Value("${search.service.url}") String searchServiceUrl) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.restTemplate = restTemplate;
        this.searchServiceUrl = searchServiceUrl;
    }

    // -------------------- SAVE USER --------------------
    public void save(String fullName, String email, String password) {
        User user = createUserObject(fullName, email, password, "ROLE_USER");
        userRepository.save(user);

        // Sync with search service
        syncUserToSearchService(user);
    }

    public void savePublisher(String fullName, String email, String hashedPassword) {
        User user = createUserObject(fullName, email, hashedPassword, "ROLE_UNIPUBLISHER");
        userRepository.save(user);

        // Sync with search service
        syncUserToSearchService(user);
    }

    private User createUserObject(String fullName, String email, String password, String roleName) {
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(password);
        user.setUserName(email);
        user.setUserMetice(0.0F);
        Role role = roleRepository.findByNameIgnoreCase(roleName)
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName(roleName);
                    return roleRepository.save(r);
                });
        user.setRole(role);
        user.setVerified(true);
        return user;
    }

    // -------------------- SYNC --------------------
    private void syncUserToSearchService(User user) {
        try {
            String url = searchServiceUrl + "/users/sync";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            UserTransferDto userToTransfer = new UserTransferDto();
            userToTransfer.setId(user.getId());
            userToTransfer.setEmail(user.getEmail());
            userToTransfer.setName(user.getFullName());
            HttpEntity<UserTransferDto> entity = new HttpEntity<>(userToTransfer, headers);
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
            log.info("User synced to search service: {}", response.getStatusCode());
        } catch (Exception e) {
            log.error("Failed to sync user to search service: {}", e.getMessage(), e);
        }
    }

    // -------------------- DELETE REFRESH TOKEN --------------------
    @Transactional
    public boolean deleteRefreshTokenForUser(String email) {
        try {
            User user = getUserByEmail(email);
            if (user != null) {
                refreshTokenRepository.deleteByUserId(user.getId());
                log.info("Refresh token deleted for user: {}", email);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Failed to delete refresh token: {}", e.getMessage());
            return false;
        }
    }

    // -------------------- OTHER METHODS --------------------
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    public boolean isEmailRegistered(String email) {
        return userRepository.existsByEmail(email);
    }

    public long getUserCount() {
        return userRepository.count();
    }

    public long getRoleCount() {
        return roleRepository.count();
    }

    @Cacheable(value = "userDtoCache", key = "#email")
    public UserDto getUserDtoByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDto dto = new UserDto();
        dto.setUsername(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setRoles(Collections.singletonList(user.getRole().getName()));
        dto.setAccountNonExpired(true);
        dto.setAccountNonLocked(true);
        dto.setCredentialsNonExpired(true);
        dto.setEnabled(user.isVerified());

        log.info("UserDto created for email: {}", email);
        return dto;
    }

    public void saveAdmin(String fullName, String email, String hashedPassword) {
        User user = createUserObject(fullName, email, hashedPassword, "ROLE_ADMIN");
        userRepository.save(user);

        // Sync with search service
        syncUserToSearchService(user);
    }
}
