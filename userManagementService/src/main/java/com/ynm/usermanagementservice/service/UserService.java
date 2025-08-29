package com.ynm.usermanagementservice.service;

import com.ynm.usermanagementservice.dto.UserDto;
import com.ynm.usermanagementservice.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

import com.ynm.usermanagementservice.model.User;
import com.ynm.usermanagementservice.model.Role;
import com.ynm.usermanagementservice.repository.UserRepository;
import com.ynm.usermanagementservice.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

//    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
//        this.userRepository = userRepository;
//        this.roleRepository = roleRepository;
//    }

    public User getUserByEmail(String email) {
        Optional<User> user =  userRepository.findByEmail(email);
        if(user.isPresent()) return user.get();
        else {
            throw new RuntimeException("User not found");
        }
    }

    public boolean isEmailRegistered(String email) {
        return userRepository.existsByEmail(email);
    }

    public void save(String fullName, String email, String password) {
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(password);
        user.setUserName(email); // Use email as username for now

        // Set default role (ROLE_USER)
        Role defaultRole = roleRepository.findByNameIgnoreCase("ROLE_USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ROLE_USER");
                    return roleRepository.save(role);
                });

        user.setRole(defaultRole);
        user.setVerified(true); // Set as verified by default

        userRepository.save(user);
    }

    public long getUserCount() {
        return userRepository.count();
    }

    public long getRoleCount() {
        return roleRepository.count();
    }
    public UserDto getUserDtoByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDto dto = new UserDto();
        dto.setUsername(user.getEmail());
        dto.setPassword(user.getPassword());
        // Assuming user has a single role
        dto.setRoles(Collections.singletonList(user.getRole().getName()));
        dto.setAccountNonExpired(true);
        dto.setAccountNonLocked(true);
        dto.setCredentialsNonExpired(true);
        dto.setEnabled(user.isVerified());

        return dto;
    }
    @Transactional
    public boolean deleteRefreshTokenForUser(String email) {
        try {
            User user = getUserByEmail(email);
            if (user != null) {
                // Find and delete refresh token by user ID
                // Assuming you have a refreshTokenRepository
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


    public void savePublisher(String fullName, String email, String hashedPassword) {
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setUserName(email); // Use email as username for now

        // Set default role (ROLE_USER)
        Role defaultRole = roleRepository.findByNameIgnoreCase("ROLE_UNIPUBLISHER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ROLE_UNIPUBLISHER");
                    return roleRepository.save(role);
                });

        user.setRole(defaultRole);
        user.setVerified(true); // Set as verified by default

        userRepository.save(user);
    }
}
