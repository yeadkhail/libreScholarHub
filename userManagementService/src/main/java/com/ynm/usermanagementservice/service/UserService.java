package com.ynm.usermanagementservice.service;

import com.ynm.usermanagementservice.model.AppUser;
import com.ynm.usermanagementservice.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AppUser register(String username, String rawPassword, Set<String> roles) {
        if (repository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            roles.add("ROLE_USER");
        }
        AppUser user = AppUser.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .roles(String.join(",", roles))
                .build();
        return repository.save(user);
    }

    public Optional<AppUser> findByUsername(String username) {
        return repository.findByUsername(username);
    }
}
