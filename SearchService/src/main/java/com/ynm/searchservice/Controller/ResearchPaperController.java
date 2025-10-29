package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.PaperTag;
import com.ynm.searchservice.Model.ResearchPaper;
import com.ynm.searchservice.service.ResearchPaperService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/research-papers")
@AllArgsConstructor
public class ResearchPaperController {

    private final ResearchPaperService researchPaperService;

    @PostMapping("/sync")
    public ResponseEntity<String> syncResearchPaper(@RequestBody ResearchPaper paper) {
        researchPaperService.syncResearchPaper(paper);
        return ResponseEntity.ok("ResearchPaper synced");
    }

    @PutMapping("/sync/{id}")
    public ResponseEntity<String> updateResearchPaper(@PathVariable Integer id,
                                                      @RequestBody ResearchPaper paper) {
        researchPaperService.updateResearchPaper(id, paper);
        return ResponseEntity.ok("ResearchPaper updated or synced");
    }

    @DeleteMapping("/sync/{id}")
    public ResponseEntity<String> deleteResearchPaper(@PathVariable Integer id) {
        researchPaperService.deleteResearchPaper(id);
        return ResponseEntity.ok("ResearchPaper removed");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResearchPaper> getPaper(@PathVariable Integer id) {
        return ResponseEntity.ok(researchPaperService.getPaper(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ResearchPaper>> searchByTitle(@RequestParam String keyword) {
        return ResponseEntity.ok(researchPaperService.searchByTitle(keyword));
    }

    @GetMapping
    public ResponseEntity<List<ResearchPaper>> getAllPapers() {
        return ResponseEntity.ok(researchPaperService.getAllPapers());
    }
    @GetMapping("/tag/{tagId}")
    public ResponseEntity<List<ResearchPaper>> getPapersByTag(@PathVariable int tagId) {
        return ResponseEntity.ok(researchPaperService.getPapersByTag(tagId));
    }
}
