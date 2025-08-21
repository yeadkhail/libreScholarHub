package com.ynm.researchpaperservice.Controller;

import com.ynm.researchpaperservice.Model.PublisherReview;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/papers/{paperId}/publisher-reviews")
@RequiredArgsConstructor
public class PublisherReviewController {

//    private final PublisherReviewService publisherReviewService;

    @PostMapping
    public ResponseEntity<PublisherReview> addPublisherReview(@PathVariable Integer paperId, @RequestBody PublisherReview review) {
//        return ResponseEntity.ok(publisherReviewService.addPublisherReview(paperId, review));
        PublisherReview dummy = new PublisherReview();
        dummy.setId(1);
        dummy.setPaper(null); // no actual paper
        dummy.setUniPubId(review.getUniPubId() != null ? review.getUniPubId() : 1001);
        return ResponseEntity.ok(dummy);
    }

    @GetMapping
    public ResponseEntity<List<PublisherReview>> getPublisherReviews(@PathVariable Integer paperId) {
//        return ResponseEntity.ok(publisherReviewService.getPublisherReviewsByPaper(paperId));
        PublisherReview dummy1 = new PublisherReview();
        dummy1.setId(1);
        dummy1.setPaper(null);
        dummy1.setUniPubId(1001);

        PublisherReview dummy2 = new PublisherReview();
        dummy2.setId(2);
        dummy2.setPaper(null);
        dummy2.setUniPubId(1002);

        List<PublisherReview> list = new ArrayList<>();
        list.add(dummy1);
        list.add(dummy2);

        return ResponseEntity.ok(list);
    }
}
