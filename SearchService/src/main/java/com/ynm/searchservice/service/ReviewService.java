package com.ynm.searchservice.service;

import com.ynm.searchservice.Model.Review;
import com.ynm.searchservice.Model.User;
import com.ynm.searchservice.Repository.ReviewRepository;
import com.ynm.searchservice.Repository.UserRepository;
import com.ynm.searchservice.dto.ReviewDto;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    //@CachePut(value = "reviews", key = "#dto.id")
    public Review syncReview(ReviewDto dto) {
        Review review = new Review();
        review.setId(dto.getId());

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        review.setUser(user);

        review.setScore(dto.getScore());
        review.setPaper(dto.getPaper());
        review.setComment(dto.getComment());
        review.setTimestamp(dto.getTimestamp());

        return reviewRepository.save(review);
    }

    //@CacheEvict(value = "reviews", key = "#id")
    public void deleteReview(Integer id) {
        if (!reviewRepository.existsById(id)) {
            throw new RuntimeException("Review not found");
        }
        reviewRepository.deleteById(id);
    }

    //@Cacheable(value = "reviewsByPaper", key = "#paperId")
    public List<Review> getReviewsByPaper(Integer paperId) {
        return reviewRepository.findByPaperId(paperId);
    }
    public List<Review> getReviewsByPaperByMetricDesc(Integer paperId) {
        return reviewRepository.findByPaperIdOrderByScoreDesc(paperId);
    }
    public List<Review> getReviewsByPaperByMetricAsc(Integer paperId) {
        return reviewRepository.findByPaperIdOrderByScoreAsc(paperId);
    }
}
