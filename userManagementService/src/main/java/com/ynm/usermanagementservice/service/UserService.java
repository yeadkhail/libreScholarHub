package com.ynm.usermanagementservice.service;

import org.springframework.stereotype.Service;

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

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

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
}
