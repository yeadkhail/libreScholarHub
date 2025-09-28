package com.ynm.usermanagementservice.controller;

import com.ynm.usermanagementservice.dto.UserDto;
import com.ynm.usermanagementservice.dto.UserScoreSyncRequest;
import com.ynm.usermanagementservice.model.User;
import com.ynm.usermanagementservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/email/{email}")
    public UserDto getUserByEmail(@PathVariable String email) {
        com.ynm.usermanagementservice.dto.UserDto dto = userService.getUserDtoByEmail(email);
        if (dto == null) {
            throw new RuntimeException("User not found with email: " + email);
        }
        else{
            System.out.println("User found with email: " + email);
        }
        return dto;
    }

    @PutMapping("/syncScore")
    public void updateUserScore(@RequestBody UserScoreSyncRequest request) {
        System.out.println("Updating user score");
        System.out.println(request);
    }

    @GetMapping("/email/{email}/score")
    public ResponseEntity<Float> getUserScoreByEmail(@PathVariable String email) {
        User user =userService.getUserByEmail(email);
        Float score = user.getUserMetice() != null ? user.getUserMetice() : 0f;
        return ResponseEntity.ok(score);
    }


}