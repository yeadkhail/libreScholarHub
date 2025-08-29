package com.ynm.researchpaperservice.Controller;

import com.ynm.researchpaperservice.Model.PublisherReview;
import com.ynm.researchpaperservice.dto.PublisherReviewDto;
import com.ynm.researchpaperservice.service.JWTServiceImpl;
import com.ynm.researchpaperservice.service.PublisherReviewService;
import com.ynm.researchpaperservice.service.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/publisher-reviews")
@RequiredArgsConstructor
public class PublisherReviewController {

    private final PublisherReviewService publisherReviewService;
    private final JWTServiceImpl jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    // --- Helper Method: Role Check ---
    private boolean hasUniPublisherRole(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {

                return false;
            }

            String token = authHeader.substring(7);
            String username = jwtService.extractUserName(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            System.out.println(userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_UNIPUBLISHER")));

            return userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_UNIPUBLISHER"));
        } catch (Exception e) {
            return false;
        }
    }
    @PostMapping("/{paperId}")
    public ResponseEntity<PublisherReview> createPubReview(@PathVariable Integer paperId,
                                                           @RequestBody PublisherReviewDto reviewDto,
                                                           HttpServletRequest request) {
        if (!hasUniPublisherRole(request)) {
            return ResponseEntity.status(403).build();
        }
        try {
            return ResponseEntity.ok(publisherReviewService.createPubReview(reviewDto, paperId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PublisherReview> updatePubReview(@PathVariable Integer id,
                                                           @RequestBody PublisherReviewDto reviewDto,
                                                           HttpServletRequest request) {
        if (!hasUniPublisherRole(request)) {
            return ResponseEntity.status(403).build();
        }
        try {
            return ResponseEntity.ok(publisherReviewService.updatePubReview(id, reviewDto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }

    // DELETE (returns deleted review)
    @DeleteMapping("/{id}")
    public ResponseEntity<PublisherReview> deletePubReview(@PathVariable Integer id,
                                                           HttpServletRequest request) {
        if (!hasUniPublisherRole(request)) {
            return ResponseEntity.status(403).build(); // Forbidden
        }
        try {
            PublisherReview deleted = publisherReviewService.deletePubReview(id);
            return ResponseEntity.ok(deleted); // return deleted entity
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build(); // not found
        }
    }
}
