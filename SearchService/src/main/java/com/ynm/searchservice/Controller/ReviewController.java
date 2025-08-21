package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.*;
import com.ynm.searchservice.Repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;

    public ReviewController(ReviewRepository repo) {
        this.reviewRepository = repo;
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncReview(@RequestBody Review review) {
        reviewRepository.save(review);
        return ResponseEntity.ok("Review synced");
    }

    @DeleteMapping("/sync/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Integer id) {
        reviewRepository.deleteById(id);
        return ResponseEntity.ok("Review removed");
    }

    @GetMapping("/paper/{paperId}")
    public ResponseEntity<List<Review>> getReviewsByPaper(@PathVariable Integer paperId) {
        return ResponseEntity.ok(reviewRepository.findByPaperId(paperId));
    }
}
