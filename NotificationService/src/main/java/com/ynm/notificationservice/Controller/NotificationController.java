package com.ynm.notificationservice.Controller;

import com.example.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

//    private final NotificationService notificationService;

    // POST /api/notifications/send
    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(
            @RequestParam String messageBody,
            @RequestParam Long userId) {

//        notificationService.sendNotification(messageBody, userId);
        return ResponseEntity.ok("Notification sent to userId: " + userId);

    }
}