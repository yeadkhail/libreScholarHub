package com.ynm.searchservice.service;

import com.ynm.searchservice.Model.User;
import com.ynm.searchservice.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    //@CachePut(value = "users", key = "#user.id")
    public User syncUser(User user) {
        return userRepository.save(user);
    }

    //@CacheEvict(value = "users", key = "#id")
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    //@Cacheable(value = "users", key = "#id")
    public User getUser(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
