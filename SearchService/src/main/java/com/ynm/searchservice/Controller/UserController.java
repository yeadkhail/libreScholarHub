package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.User;
import com.ynm.searchservice.Repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository repo) {
        this.userRepository = repo;
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncUser(@RequestBody User user) {
        userRepository.save(user);
        return ResponseEntity.ok("User synced");
    }

    @DeleteMapping("/sync/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok("User removed");
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Integer id) {
        return ResponseEntity.of(userRepository.findById(id));
    }
}
