package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.PublisherReview;
import com.ynm.searchservice.dto.PublisherReviewDto;
import com.ynm.searchservice.service.PublisherReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/publisher-reviews")
@AllArgsConstructor
public class PublisherReviewController {

    private final PublisherReviewService publisherReviewService;

    @PostMapping("/sync")
    public ResponseEntity<String> syncPublisherReview(@RequestBody PublisherReviewDto dto) {
        publisherReviewService.syncPublisherReview(dto);
        return ResponseEntity.ok("PublisherReview synced");
    }

    @DeleteMapping("/sync/{id}")
    public ResponseEntity<String> deletePublisherReview(@PathVariable Integer id) {
        publisherReviewService.deletePublisherReview(id);
        return ResponseEntity.ok("PublisherReview removed");
    }

    @GetMapping("/paper/{paperId}")
    public ResponseEntity<List<PublisherReview>> getPublisherReviewsByPaper(@PathVariable Integer paperId) {
        return ResponseEntity.ok(publisherReviewService.getPublisherReviewsByPaper(paperId));
    }
}
