package com.ynm.researchpaperservice.service;

import com.ynm.researchpaperservice.Model.Review;
import com.ynm.researchpaperservice.Model.ResearchPaper;
import com.ynm.researchpaperservice.Repository.ReviewRepository;
import com.ynm.researchpaperservice.Repository.ResearchPaperRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ResearchPaperRepository researchPaperRepository;
    private final RestTemplate restTemplate;
    private final String searchServiceUrl;

    public ReviewService(ReviewRepository reviewRepository,
                         ResearchPaperRepository researchPaperRepository,
                         RestTemplate restTemplate,
                         @Value("${search.service.url}") String searchServiceUrl) {
        this.reviewRepository = reviewRepository;
        this.researchPaperRepository = researchPaperRepository;
        this.restTemplate = restTemplate;
        this.searchServiceUrl = searchServiceUrl;
    }

    // CREATE
    public Review createReview(Review review, Integer paperId) {
        ResearchPaper paper = researchPaperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Research paper with id " + paperId + " not found."));

        review.setPaper(paper);
        review.setTimestamp(new Date());
        Review savedReview = reviewRepository.save(review);

        // Sync to SearchService
        syncReviewToSearchService(savedReview);

        return savedReview;
    }

    // UPDATE
    public Review updateReview(Integer id, Review updatedReview) {
        Review existing = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if(updatedReview.getScore() != null) existing.setScore(updatedReview.getScore());
        if(updatedReview.getComment() != null) existing.setComment(updatedReview.getComment());
        if(updatedReview.getTimestamp() != null) existing.setTimestamp(new Date());

        if (updatedReview.getPaper() != null) {
            Integer paperId = updatedReview.getPaper().getId();
            ResearchPaper paper = researchPaperRepository.findById(paperId)
                    .orElseThrow(() -> new RuntimeException("Research paper not found"));
            existing.setPaper(paper);
        }

        Review saved = reviewRepository.save(existing);

        syncReviewToSearchService(saved);

        return saved;
    }

    // DELETE
    public Review deleteReview(Integer id) {
        Review existing = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review with id " + id + " not found."));
        reviewRepository.delete(existing);

        // Sync deletion to SearchService
        deleteReviewFromSearchService(id);

        return existing;
    }

    // GET by ID
    public Review getReviewById(Integer id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review with id " + id + " not found."));
    }

    // GET all
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    // ---------------------- Helper methods for SearchService ----------------------

    private void syncReviewToSearchService(Review review) {
        try {
            String url = searchServiceUrl + "/reviews/sync";
            log.debug("Syncing review to Search Service at: {}", url);

            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            if (attrs != null) {
                String bearerToken = attrs.getRequest().getHeader("Authorization");
                if (bearerToken != null && !bearerToken.isEmpty()) {
                    headers.set("Authorization", bearerToken);
                }
            }

            HttpEntity<Review> entity = new HttpEntity<>(review, headers);
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);

            log.debug("Review sync response: {}", response.getStatusCode());
        } catch (Exception e) {
            log.error("Failed to sync review with Search Service: {}", e.getMessage(), e);
        }
    }

    private void deleteReviewFromSearchService(Integer reviewId) {
        try {
            String url = searchServiceUrl + "/reviews/sync/" + reviewId;
            log.debug("Deleting review in Search Service at: {}", url);

            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            if (attrs != null) {
                String bearerToken = attrs.getRequest().getHeader("Authorization");
                if (bearerToken != null && !bearerToken.isEmpty()) {
                    headers.set("Authorization", bearerToken);
                }
            }

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);

            log.debug("Review delete sync response: {}", response.getStatusCode());
        } catch (Exception e) {
            log.error("Failed to delete review in Search Service: {}", e.getMessage(), e);
        }
    }
}
