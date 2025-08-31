package com.ynm.researchpaperservice.service;

import com.ynm.researchpaperservice.Model.PublisherReview;
import com.ynm.researchpaperservice.Model.ResearchPaper;
import com.ynm.researchpaperservice.Repository.PublisherReviewRepository;
import com.ynm.researchpaperservice.Repository.ResearchPaperRepository;
import com.ynm.researchpaperservice.dto.PublisherReviewDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublisherReviewService {

    private final PublisherReviewRepository publisherReviewRepository;
    private final ResearchPaperRepository researchPaperRepository;
    private final RestTemplate restTemplate;

    @Value("${search.service.url}")
    private String searchServiceUrl;

    // CREATE
    public PublisherReview createPubReview(PublisherReviewDto reviewDto, Integer paperId) {
        ResearchPaper paper = researchPaperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Research paper with id " + paperId + " not found."));

        PublisherReview review = new PublisherReview();
        review.setUniPubId(reviewDto.getUniPubId());
        review.setReviewScore(reviewDto.getReviewScore());
        review.setReviewText(reviewDto.getReviewText());
        review.setPaper(paper);

        PublisherReview saved = publisherReviewRepository.save(review);

        // Sync to searchservice
        syncWithSearchService(saved);

        return saved;
    }

    // UPDATE
    public PublisherReview updatePubReview(Integer id, PublisherReviewDto reviewDto) {
        PublisherReview existing = publisherReviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PublisherReview with id " + id + " not found."));

        if(reviewDto.getUniPubId()!=null) existing.setUniPubId(reviewDto.getUniPubId());
        if(reviewDto.getReviewScore()!=null) existing.setReviewScore(reviewDto.getReviewScore());
        if(reviewDto.getReviewText()!=null) existing.setReviewText(reviewDto.getReviewText());

        PublisherReview saved = publisherReviewRepository.save(existing);

        // Sync to searchservice
        syncWithSearchService(saved);

        return saved;
    }

    // DELETE
    public PublisherReview deletePubReview(Integer id) {
        PublisherReview existing = publisherReviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PublisherReview with id " + id + " not found."));
        publisherReviewRepository.delete(existing);

        // Sync deletion to searchservice
        deleteFromSearchService(existing);

        return existing;
    }

    // --- Helper methods to sync with searchservice ---

    private void syncWithSearchService(PublisherReview review) {
        try {
            String url = searchServiceUrl + "/publisher-reviews/sync";
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            if (attrs != null) {
                String bearerToken = attrs.getRequest().getHeader("Authorization");
                if (bearerToken != null && !bearerToken.isEmpty()) {
                    headers.set("Authorization", bearerToken);
                }
            }

            HttpEntity<PublisherReview> entity = new HttpEntity<>(review, headers);
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
            log.debug("PublisherReview synced with searchservice, status: {}", response.getStatusCode());

        } catch (Exception e) {
            log.error("Failed to sync PublisherReview with searchservice: {}", e.getMessage(), e);
        }
    }

    private void deleteFromSearchService(PublisherReview review) {
        try {
            String url = searchServiceUrl + "/publisher-reviews/sync/" + review.getId();
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpHeaders headers = new HttpHeaders();

            if (attrs != null) {
                String bearerToken = attrs.getRequest().getHeader("Authorization");
                if (bearerToken != null && !bearerToken.isEmpty()) {
                    headers.set("Authorization", bearerToken);
                }
            }

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
            log.debug("PublisherReview deletion synced with searchservice, status: {}", response.getStatusCode());

        } catch (Exception e) {
            log.error("Failed to delete PublisherReview in searchservice: {}", e.getMessage(), e);
        }
    }
}
