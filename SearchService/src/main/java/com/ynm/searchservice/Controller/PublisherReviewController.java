package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.*;
import com.ynm.searchservice.Repository.*;
import com.ynm.searchservice.dto.PublisherReviewDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/publisher-reviews")
public class PublisherReviewController {

    private final PublisherReviewRepository publisherReviewRepository;
    private final UserRepository userRepository;
    private final ResearchPaperRepository paperRepository;

    public PublisherReviewController(PublisherReviewRepository repo,
                                     UserRepository userRepository,
                                     ResearchPaperRepository paperRepository) {
        this.publisherReviewRepository = repo;
        this.userRepository = userRepository;
        this.paperRepository = paperRepository;
    }

    // Sync create/update from researchpaperservice
    @PostMapping("/sync")
    public ResponseEntity<String> syncPublisherReview(@RequestBody PublisherReviewDto dto) {
        PublisherReview pr = new PublisherReview();
        pr.setId(dto.getId());

        userRepository.findById(dto.getUniPubId()).ifPresent(pr::setUniPub);

        pr.setReviewText(dto.getReviewText());
        pr.setReviewScore(dto.getReviewScore());
        pr.setPaper(dto.getPaper());
        publisherReviewRepository.save(pr);
        return ResponseEntity.ok("PublisherReview synced");
    }

    // Delete
    @DeleteMapping("/sync/{id}")
    public ResponseEntity<String> deletePublisherReview(@PathVariable Integer id) {
        publisherReviewRepository.deleteById(id);
        return ResponseEntity.ok("PublisherReview removed");
    }

    // Get all reviews of a paper
    @GetMapping("/paper/{paperId}")
    public ResponseEntity<List<PublisherReview>> getPublisherReviewsByPaper(@PathVariable Integer paperId) {
        return ResponseEntity.ok(publisherReviewRepository.findByPaperId(paperId));
    }
}
