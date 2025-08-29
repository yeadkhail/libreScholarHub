package com.ynm.researchpaperservice.service;

import com.ynm.researchpaperservice.Model.PublisherReview;
import com.ynm.researchpaperservice.Model.ResearchPaper;
import com.ynm.researchpaperservice.Repository.PublisherReviewRepository;
import com.ynm.researchpaperservice.Repository.ResearchPaperRepository;
import com.ynm.researchpaperservice.dto.PublisherReviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublisherReviewService {

    private final PublisherReviewRepository publisherReviewRepository;
    private final ResearchPaperRepository researchPaperRepository;

    public PublisherReview createPubReview(PublisherReviewDto reviewDto, Integer paperId) {
        ResearchPaper paper = researchPaperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Research paper with id " + paperId + " not found."));

        PublisherReview review = new PublisherReview();
        review.setUniPubId(reviewDto.getUniPubId());
        review.setReviewScore(reviewDto.getReviewScore());
        review.setReviewScore(reviewDto.getReviewScore());
        review.setPaper(paper);

        return publisherReviewRepository.save(review);
    }

    public PublisherReview updatePubReview(Integer id, PublisherReviewDto reviewDto) {
        PublisherReview existing = publisherReviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PublisherReview with id " + id + " not found."));

        existing.setUniPubId(reviewDto.getUniPubId());
        return publisherReviewRepository.save(existing);
    }


    public PublisherReview deletePubReview(Integer id) {
        PublisherReview existing = publisherReviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PublisherReview with id " + id + " not found."));
        publisherReviewRepository.delete(existing);
        return existing;
    }
}
