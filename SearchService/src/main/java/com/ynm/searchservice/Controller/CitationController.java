package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.Citation;
import com.ynm.searchservice.Repository.CitationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/citations")
public class CitationController {

    private final CitationRepository citationRepository;

    public CitationController(CitationRepository repo) {
        this.citationRepository = repo;
    }

    /** Sync a new citation */
    @PostMapping("/sync")
    public ResponseEntity<String> syncCitation(@RequestBody Citation citation) {
        citationRepository.save(citation);
        return ResponseEntity.ok("Citation synced");
    }

    /** Update an existing citation */
    @PutMapping("/sync/{id}")
    public ResponseEntity<String> updateCitation(@PathVariable Long id, @RequestBody Citation citation) {
        Optional<Citation> existing = citationRepository.findById(id);
        if (existing.isPresent()) {
            Citation c = existing.get();
            c.setCitedPaper(citation.getCitedPaper());
            c.setCitingPaper(citation.getCitingPaper());
            citationRepository.save(c);
            return ResponseEntity.ok("Citation updated");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /** Delete a citation by ID */
    @DeleteMapping("/sync/{id}")
    public ResponseEntity<String> deleteCitation(@PathVariable Long id) {
        if (citationRepository.existsById(id)) {
            citationRepository.deleteById(id);
            return ResponseEntity.ok("Citation removed");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /** Get citations where a paper is citing others */
    @GetMapping("/paper/{paperId}/citing")
    public ResponseEntity<List<Citation>> getCitationsByCitingPaper(@PathVariable Integer paperId) {
        return ResponseEntity.ok(citationRepository.findByCitingPaperId(paperId));
    }

    /** Get citations where a paper is cited by others */
    @GetMapping("/paper/{paperId}/cited")
    public ResponseEntity<List<Citation>> getCitationsByCitedPaper(@PathVariable Integer paperId) {
        return ResponseEntity.ok(citationRepository.findByCitedPaperId(paperId));
    }
}
