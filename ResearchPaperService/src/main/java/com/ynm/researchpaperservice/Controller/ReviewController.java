package com.ynm.researchpaperservice.Controller;

import com.ynm.researchpaperservice.Model.Review;
import com.ynm.researchpaperservice.dto.UserDto;
import com.ynm.researchpaperservice.service.ReviewService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // CREATE
    @PostMapping("/{paperId}")
    public ResponseEntity<Review> createReview(@PathVariable Integer paperId,
                                               @RequestBody Review review) {
        try {

            return ResponseEntity.ok(reviewService.createReview(review, paperId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build(); // Paper not found
        }
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable Integer id,
                                               @RequestBody Review updatedReview) {
        try {
            return ResponseEntity.ok(reviewService.updateReview(id, updatedReview));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }

    // DELETE (return deleted review)
    @DeleteMapping("/{id}")
    public ResponseEntity<Review> deleteReview(@PathVariable Integer id) {
        try {
            Review deleted = reviewService.deleteReview(id);
            return ResponseEntity.ok(deleted);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(reviewService.getReviewById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }

    // GET all
    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }
}
