package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.*;
import com.ynm.searchservice.Repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/publisher-reviews")
public class PublisherReviewController {

    private final PublisherReviewRepository publisherReviewRepository;

    public PublisherReviewController(PublisherReviewRepository repo) {
        this.publisherReviewRepository = repo;
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncPublisherReview(@RequestBody PublisherReview pr) {
        publisherReviewRepository.save(pr);
        return ResponseEntity.ok("PublisherReview synced");
    }

    @DeleteMapping("/sync/{id}")
    public ResponseEntity<String> deletePublisherReview(@PathVariable Integer id) {
        publisherReviewRepository.deleteById(id);
        return ResponseEntity.ok("PublisherReview removed");
    }

    @GetMapping("/paper/{paperId}")
    public ResponseEntity<List<PublisherReview>> getPublisherReviewsByPaper(@PathVariable Integer paperId) {
        return ResponseEntity.ok(publisherReviewRepository.findByPaperId(paperId));
    }
}
