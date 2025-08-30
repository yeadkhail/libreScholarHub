package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.ResearchPaper;
import com.ynm.searchservice.Repository.ResearchPaperRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/research-papers")
public class ResearchPaperController {

    private final ResearchPaperRepository researchPaperRepository;

    public ResearchPaperController(ResearchPaperRepository repo) {
        this.researchPaperRepository = repo;
    }

    // Called by ResearchPaperService to create or sync a paper
    @PostMapping("/sync")
    public ResponseEntity<String> syncResearchPaper(@RequestBody ResearchPaper paper) {
        researchPaperRepository.save(paper); // saves or updates
        return ResponseEntity.ok("ResearchPaper synced");
    }

    // Called by ResearchPaperService to update a paper
    @PutMapping("/sync/{id}")
    public ResponseEntity<String> updateResearchPaper(@PathVariable Integer id,
                                                      @RequestBody ResearchPaper paper) {
        return researchPaperRepository.findById(id)
                .map(existing -> {
                    if (paper.getTitle() != null) existing.setTitle(paper.getTitle());
                    if (paper.getAbstractText() != null) existing.setAbstractText(paper.getAbstractText());
                    if (paper.getUploadPath() != null) existing.setUploadPath(paper.getUploadPath());
                    if (paper.getVisibility() != null) existing.setVisibility(paper.getVisibility());
                    if (paper.getOwnerId() != null) existing.setOwnerId(paper.getOwnerId());
                    if (paper.getCreatedAt() != null) existing.setCreatedAt(paper.getCreatedAt());
                    if (paper.getMetric() != null) existing.setMetric(paper.getMetric());

                    researchPaperRepository.save(existing);
                    return ResponseEntity.ok("ResearchPaper updated in search DB");
                })
                .orElseGet(() -> {
                    researchPaperRepository.save(paper); // fallback if not exists
                    return ResponseEntity.ok("ResearchPaper synced (created new)");
                });
    }

    // Called by ResearchPaperService to delete a paper
    @DeleteMapping("/sync/{id}")
    public ResponseEntity<String> deleteResearchPaper(@PathVariable Integer id) {
        if (researchPaperRepository.existsById(id)) {
            researchPaperRepository.deleteById(id);
            return ResponseEntity.ok("ResearchPaper removed");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Get paper by ID
    @GetMapping("/{id}")
    public ResponseEntity<ResearchPaper> getPaper(@PathVariable Integer id) {
        return ResponseEntity.of(researchPaperRepository.findById(id));
    }

    // Optional: Search by title (for search service querying)
    @GetMapping("/search")
    public ResponseEntity<List<ResearchPaper>> searchByTitle(@RequestParam String keyword) {
        return ResponseEntity.ok(researchPaperRepository.findByTitleContainingIgnoreCase(keyword));
    }

    // Optional: Get all papers
    @GetMapping
    public ResponseEntity<List<ResearchPaper>> getAllPapers() {
        return ResponseEntity.ok(researchPaperRepository.findAll());
    }
}
