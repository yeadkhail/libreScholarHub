package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.*;
import com.ynm.searchservice.Repository.*;
import com.ynm.searchservice.dto.ReviewDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ResearchPaperRepository paperRepository;

    public ReviewController(ReviewRepository reviewRepository,
                            UserRepository userRepository,
                            ResearchPaperRepository paperRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.paperRepository = paperRepository;
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncReview(@RequestBody ReviewDto dto) {
        Review review = new Review();
        review.setId(dto.getId());

        // Map User
        userRepository.findById(dto.getUserId()).ifPresent(review::setUser);

        review.setScore(dto.getScore());
        review.setPaper(dto.getPaper());
        review.setComment(dto.getComment());
        review.setTimestamp(dto.getTimestamp());

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
