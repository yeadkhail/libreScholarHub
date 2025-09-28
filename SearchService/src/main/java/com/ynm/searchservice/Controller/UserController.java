package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.User;
import com.ynm.searchservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/sync")
    public ResponseEntity<String> syncUser(@RequestBody User user) {
        userService.syncUser(user);
        return ResponseEntity.ok("User synced");
    }

    @DeleteMapping("/sync/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User removed");
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUser(id));
    }
}
