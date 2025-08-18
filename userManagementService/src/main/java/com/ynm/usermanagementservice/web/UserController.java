package com.ynm.usermanagementservice.web;

import com.ynm.usermanagementservice.model.AppUser;
import com.ynm.usermanagementservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        AppUser appUser = userService.findByUsername(principal.getUsername()).orElse(null);
        if (appUser == null) return ResponseEntity.status(404).build();
        return ResponseEntity.ok(Map.of(
                "id", appUser.getId(),
                "username", appUser.getUsername(),
                "roles", appUser.roleSet()
        ));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> allUsers() {
        // For brevity, return 204 to indicate protected endpoint exists
        return ResponseEntity.noContent().build();
    }
}
