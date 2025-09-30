package com.ynm.searchservice.service;

import com.ynm.searchservice.Model.User;
import com.ynm.searchservice.Repository.UserRepository;
import com.ynm.searchservice.dto.UserScoreSyncRequest;
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
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
    public void syncUserScore(UserScoreSyncRequest request) {
//        System.out.println(request.getUserId());
        User user = userRepository.findById(request.getUserId()).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + request.getUserId());
        }
        float oldScore = user.getUserScore();
        oldScore = oldScore + request.getNewUpdate() - request.getLastUpdate();
        if(oldScore < 0){
            oldScore = 0;
        }
        user.setUserScore(oldScore);

        userRepository.save(user);
    }
    //@Cacheable(value = "users", key = "#id")
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
