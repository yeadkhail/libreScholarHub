package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.*;
import com.ynm.searchservice.Repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/papers")
public class ResearchPaperController {

    private final ResearchPaperRepository researchPaperRepository;

    public ResearchPaperController(ResearchPaperRepository repo) {
        this.researchPaperRepository = repo;
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncResearchPaper(@RequestBody ResearchPaper paper) {
        researchPaperRepository.save(paper);
        return ResponseEntity.ok("ResearchPaper synced");
    }

    @DeleteMapping("/sync/{id}")
    public ResponseEntity<String> deleteResearchPaper(@PathVariable Integer id) {
        researchPaperRepository.deleteById(id);
        return ResponseEntity.ok("ResearchPaper removed");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResearchPaper> getPaper(@PathVariable Integer id) {
        return ResponseEntity.of(researchPaperRepository.findById(id));
    }

//    @GetMapping("/search")
//    public ResponseEntity<List<ResearchPaper>> searchByTitle(@RequestParam String keyword) {
//        return ResponseEntity.ok(researchPaperRepository.findByTitleContainingIgnoreCase(keyword));
//    }
}
