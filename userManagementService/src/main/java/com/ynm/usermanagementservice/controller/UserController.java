package com.ynm.usermanagementservice.controller;

import com.ynm.usermanagementservice.dto.AssignUserScoreDto;
import com.ynm.usermanagementservice.dto.UserDto;
import com.ynm.usermanagementservice.dto.UserScoreSyncRequest;
import com.ynm.usermanagementservice.model.User;
import com.ynm.usermanagementservice.repository.UserRepository;
import com.ynm.usermanagementservice.service.JWTService;
import com.ynm.usermanagementservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;

    public UserController(UserService userService, UserRepository userRepository, JWTService jwtService, UserDetailsService userDetailsService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
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
        System.out.println(request.getLastUpdate());
        System.out.println(request.getUserId());
        System.out.println(request.getNewUpdate());
        User user = userRepository.findById(request.getUserId()).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + request.getUserId());
        }
        float oldScore = user.getUserMetice();
        oldScore = oldScore + request.getNewUpdate() - request.getLastUpdate();
        if(oldScore < 0){
            oldScore = 0;
        }
        user.setUserMetice(oldScore);

        userRepository.save(user);



    }

    @GetMapping("/email/{email}/score")
    public Float getUserScoreByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }
        return user.getUserMetice();  // returns Float
    }

    @GetMapping("/email/{email}/id")
    public Long getUserIdByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }
        return user.getId();
    }

    @PutMapping("/assign-user-score")
    public void assignUserScore(@RequestBody AssignUserScoreDto userScoreDto) {
        String userName = "";
        // Extract JWT token and username
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7);
                userName = jwtService.extractUserName(jwt);
            }
        }

        UserDto userDto = (UserDto) userDetailsService.loadUserByUsername(userName);

        String userRole = userDto.getAuthorities().stream()
                .map(Object::toString)
                .filter(role -> role.equals("ROLE_ADMIN"))
                .findFirst()
                .orElse(null);

        if (userRole == null) {
            throw new RuntimeException("Only ADMIN can assign user score.");
        }

        User user = userRepository.findById(userScoreDto.getUserId()).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + userScoreDto.getUserId());
        }

        user.setUserMetice(userScoreDto.getScore());
        userRepository.save(user);
    }

}