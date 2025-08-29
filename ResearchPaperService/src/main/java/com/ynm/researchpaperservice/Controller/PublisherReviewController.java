package com.ynm.researchpaperservice.Controller;

import com.ynm.researchpaperservice.Model.PublisherReview;
import com.ynm.researchpaperservice.dto.PublisherReviewDto;
import com.ynm.researchpaperservice.service.JWTServiceImpl;
import com.ynm.researchpaperservice.service.PublisherReviewService;
import com.ynm.researchpaperservice.service.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/publisher-reviews")
@RequiredArgsConstructor
public class PublisherReviewController {

    private final PublisherReviewService publisherReviewService;

    @PostMapping("/{paperId}")
    public ResponseEntity<PublisherReview> createPubReview(@PathVariable Integer paperId,
                                                           @RequestBody PublisherReviewDto reviewDto,
                                                           @AuthenticationPrincipal UserDetails user) {
        if(user.getAuthorities().contains("ROLE_UNIPUBLISHER")){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            return ResponseEntity.ok(publisherReviewService.createPubReview(reviewDto, paperId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PublisherReview> updatePubReview(@PathVariable Integer id,
                                                           @RequestBody PublisherReviewDto reviewDto,
                                                           @AuthenticationPrincipal UserDetails user) {
        if(user.getAuthorities().contains("ROLE_UNIPUBLISHER")){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            return ResponseEntity.ok(publisherReviewService.updatePubReview(id, reviewDto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }

    // DELETE (returns deleted review)
    @DeleteMapping("/{id}")
    public ResponseEntity<PublisherReview> deletePubReview(@PathVariable Integer id,
                                                           @AuthenticationPrincipal UserDetails user) {
        if(user.getAuthorities().contains("ROLE_UNIPUBLISHER")){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            PublisherReview deleted = publisherReviewService.deletePubReview(id);
            return ResponseEntity.ok(deleted); // return deleted entity
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build(); // not found
        }
    }
}
