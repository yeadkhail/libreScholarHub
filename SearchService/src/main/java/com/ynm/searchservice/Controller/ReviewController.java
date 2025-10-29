package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.Review;
import com.ynm.searchservice.dto.ReviewDto;
import com.ynm.searchservice.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/sync")
    public ResponseEntity<String> syncReview(@RequestBody ReviewDto dto) {
        reviewService.syncReview(dto);
        return ResponseEntity.ok("Review synced");
    }

    @DeleteMapping("/sync/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Integer id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok("Review removed");
    }

    @GetMapping("/paper/{paperId}")
    public ResponseEntity<List<Review>> getReviewsByPaper(@PathVariable Integer paperId) {
        return ResponseEntity.ok(reviewService.getReviewsByPaper(paperId));
    }
    // --- NEW ENDPOINT 1 ---
    @GetMapping("/paper/{paperId}/sorted/desc")
    public ResponseEntity<List<Review>> getReviewsByPaperDesc(@PathVariable Integer paperId) {
        return ResponseEntity.ok(reviewService.getReviewsByPaperByMetricDesc(paperId));
    }

    // --- NEW ENDPOINT 2 ---
    @GetMapping("/paper/{paperId}/sorted/asc")
    public ResponseEntity<List<Review>> getReviewsByPaperAsc(@PathVariable Integer paperId) {
        return ResponseEntity.ok(reviewService.getReviewsByPaperByMetricAsc(paperId));
    }
}
