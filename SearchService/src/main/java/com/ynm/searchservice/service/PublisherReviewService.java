package com.ynm.searchservice.service;

import com.ynm.searchservice.Model.PublisherReview;
import com.ynm.searchservice.Model.User;
import com.ynm.searchservice.Repository.PublisherReviewRepository;
import com.ynm.searchservice.Repository.UserRepository;
import com.ynm.searchservice.dto.PublisherReviewDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PublisherReviewService {

    private final PublisherReviewRepository publisherReviewRepository;
    private final UserRepository userRepository;

    public PublisherReview syncPublisherReview(PublisherReviewDto dto) {
        PublisherReview pr = new PublisherReview();
        pr.setId(dto.getId());

        User uniPub = userRepository.findById(dto.getUniPubId())
                .orElseThrow(() -> new RuntimeException("User (UniPub) not found"));
        pr.setUniPub(uniPub);

        pr.setReviewText(dto.getReviewText());
        pr.setReviewScore(dto.getReviewScore());
        pr.setPaper(dto.getPaper());

        return publisherReviewRepository.save(pr);
    }

    public void deletePublisherReview(Integer id) {
        if (!publisherReviewRepository.existsById(id)) {
            throw new RuntimeException("PublisherReview not found");
        }
        publisherReviewRepository.deleteById(id);
    }

    public List<PublisherReview> getPublisherReviewsByPaper(Integer paperId) {
        return publisherReviewRepository.findByPaperId(paperId);
    }
}
