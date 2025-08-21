package com.ynm.researchpaperservice.Controller;

import com.ynm.researchpaperservice.Entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

@RestController
@RequestMapping("/papers/{paperId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

//    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> addReview(@PathVariable Integer paperId, @RequestBody Review review) {
//        return ResponseEntity.ok(reviewService.addReview(paperId, review));
        Review dummy = new Review();
        dummy.setId(1);
        dummy.setPaper(null); // no actual paper
        dummy.setId(review.getId() != null ? review.getId() : 1001);
        dummy.setScore(review.getScore() != null ? review.getScore() : 5);
        dummy.setComment(review.getComment() != null ? review.getComment() : "Dummy comment");
        dummy.setTimestamp(new Date());
        return ResponseEntity.ok(dummy);
    }

    @GetMapping
    public ResponseEntity<List<Review>> getReviews(@PathVariable Integer paperId) {
//        return ResponseEntity.ok(reviewService.getReviewsByPaper(paperId));
        Review dummy1 = new Review();
        dummy1.setId(1);
        dummy1.setPaper(null);
        dummy1.setId(1001);
        dummy1.setScore(5);
        dummy1.setComment("Excellent paper");
        dummy1.setTimestamp(new Date());

        Review dummy2 = new Review();
        dummy2.setId(2);
        dummy2.setPaper(null);
        dummy2.setId(1002);
        dummy2.setScore(4);
        dummy2.setComment("Good paper");
        dummy2.setTimestamp(new Date());

        List<Review> list = new ArrayList<>();
        list.add(dummy1);
        list.add(dummy2);

        return ResponseEntity.ok(list);
    }
}
