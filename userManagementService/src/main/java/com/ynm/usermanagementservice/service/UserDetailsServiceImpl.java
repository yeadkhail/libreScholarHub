package com.ynm.usermanagementservice.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import com.ynm.usermanagementservice.repository.UserRepository;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to load user by username/email: {}", username);

        // Since login uses email, we need to find by email
        var userOptional = userRepository.findByEmail(username);

        if (userOptional.isPresent()) {
            log.info("User found: {}", username);
            return userOptional.get();
        } else {
            log.warn("User not found with email: {}", username);
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
    }
}

