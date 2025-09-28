package com.ynm.researchpaperservice.Controller;

import com.ynm.researchpaperservice.service.UserScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test-sync")
@RequiredArgsConstructor
public class TestUserScoreController {

    private final UserScoreService userScoreService;

    @PostMapping("/user-score")
    public void syncUserScore(
            @RequestParam String email,
            @RequestParam Float newUpdate,
            @RequestParam Float lastUpdate) {
        userScoreService.syncScore(email, newUpdate, lastUpdate);
    }
    @GetMapping("/get-by-email")
    public Float getUserScoreByEmail(@RequestParam String email) {
        return userScoreService.getUserScoreByEmail(email);
    }

}
