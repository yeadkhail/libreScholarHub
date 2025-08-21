package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.*;
import com.ynm.searchservice.Repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/citations")
public class CitationController {

    private final CitationRepository citationRepository;

    public CitationController(CitationRepository repo) {
        this.citationRepository = repo;
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncCitation(@RequestBody Citation citation) {
        citationRepository.save(citation);
        return ResponseEntity.ok("Citation synced");
    }

    @DeleteMapping("/sync/{id}")
    public ResponseEntity<String> deleteCitation(@PathVariable Long id) {
        citationRepository.deleteById(id);
        return ResponseEntity.ok("Citation removed");
    }

    @GetMapping("/paper/{paperId}/cited")
    public ResponseEntity<List<Citation>> getCitationsForPaper(@PathVariable Integer paperId) {
        return ResponseEntity.ok(citationRepository.findByCitingPaperId(paperId));
    }
    @GetMapping("/paper/{paperId}/citing")
    public ResponseEntity<List<Citation>> getCitedFromPaper(@PathVariable Integer paperId) {
        return ResponseEntity.ok(citationRepository.findByCitedPaperId(paperId));
    }
}
