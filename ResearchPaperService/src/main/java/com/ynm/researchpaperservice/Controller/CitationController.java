package com.ynm.researchpaperservice.Controller;

import com.ynm.researchpaperservice.Model.Citation;
import com.ynm.researchpaperservice.service.CitationService;
import com.ynm.researchpaperservice.dto.CitationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/citations")
@RequiredArgsConstructor
public class CitationController {

    private final CitationService citationService;

    @PostMapping
    public ResponseEntity<?> addCitation(@RequestBody CitationDto request) {
        try {
            Citation created = citationService.createCitation(
                    request.getCitedPaperId(),
                    request.getCitingPaperId()
            );

            if (created == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Invalid paper IDs: one or both research papers do not exist")
                );
            }
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "An unexpected error occurred while creating citation")
            );
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getCitationById(@PathVariable Long id) {
        try {
            Citation citation = citationService.getCitationById(id);
            if (citation != null) {
                return ResponseEntity.ok(citation);
            } else {
                return ResponseEntity.status(404).body(
                        Map.of("error", "Citation with id " + id + " not found")
                );
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "An unexpected error occurred while fetching citation")
            );
        }
    }
    @GetMapping("/cited/{paperId}")
    public ResponseEntity<List<Citation>> getCitationsOfPaper(@PathVariable Integer paperId) {
        try {
            List<Citation> citations = citationService.getCitationsByCitedPaperId(paperId)
                    .stream().toList();
            return ResponseEntity.ok(citations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
  // Get all citations made by a specific citing paper
    @GetMapping("/citing/{paperId}")
    public ResponseEntity<List<Citation>> getCitationsByPaper(@PathVariable Integer paperId) {
        try {
            List<Citation> citations = citationService.getCitationsByCitingPaperId(paperId)
                    .stream().toList();
            return ResponseEntity.ok(citations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update a citation
//    @PutMapping("/{id}")
//    public ResponseEntity<Citation> updateCitation(
//            @PathVariable Long id,
//            @RequestBody CitationDto citationDto) {
//        try {
//            Citation updated = citationService.updateCitation(id, citationDto);
//            if (updated != null) {
//                return ResponseEntity.ok(updated);
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }


    // Delete a citation
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Citation> deleteCitation(@PathVariable Long id) {
//        try {
//            Citation deleted = citationService.deleteCitation(id);
//            if (deleted != null) {
//                return ResponseEntity.ok(deleted);
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
}
