package com.ynm.researchpaperservice.service;

import com.ynm.researchpaperservice.Model.Review;
import com.ynm.researchpaperservice.Model.ResearchPaper;
import com.ynm.researchpaperservice.Repository.ReviewRepository;
import com.ynm.researchpaperservice.Repository.ResearchPaperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ResearchPaperRepository researchPaperRepository;

    // CREATE
    public Review createReview(Review review, Integer paperId) {
        ResearchPaper paper = researchPaperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Research paper with id " + paperId + " not found."));

        review.setPaper(paper);
        review.setTimestamp(new Date()); // set current time
        return reviewRepository.save(review);
    }

    // UPDATE
    public Review updateReview(Integer id, Review updatedReview) {
        Review existing = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review with id " + id + " not found."));

        existing.setScore(updatedReview.getScore());
        existing.setComment(updatedReview.getComment());
        existing.setTimestamp(new Date()); // update timestamp

        if (updatedReview.getPaper() != null) {
            Integer paperId = updatedReview.getPaper().getId();
            ResearchPaper paper = researchPaperRepository.findById(paperId)
                    .orElseThrow(() -> new RuntimeException("Research paper with id " + paperId + " not found."));
            existing.setPaper(paper);
        }

        return reviewRepository.save(existing);
    }

    // DELETE
    public Review deleteReview(Integer id) {
        Review existing = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review with id " + id + " not found."));
        reviewRepository.delete(existing);
        return existing;
    }

    // GET by ID
    public Review getReviewById(Integer id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review with id " + id + " not found."));
    }

    // GET all
    public java.util.List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
}
