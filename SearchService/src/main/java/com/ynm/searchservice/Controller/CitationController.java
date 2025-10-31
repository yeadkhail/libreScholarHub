package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.Citation;
import com.ynm.searchservice.service.CitationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search/citations")
@AllArgsConstructor
public class CitationController {

    private final CitationService citationService;

    @PostMapping("/sync")
    public ResponseEntity<String> syncCitation(@RequestBody Citation citation) {
        citationService.syncCitation(citation);
        return ResponseEntity.ok("Citation synced");
    }

    @PutMapping("/sync/{id}")
    public ResponseEntity<String> updateCitation(@PathVariable Long id, @RequestBody Citation citation) {
        citationService.updateCitation(id, citation);
        return ResponseEntity.ok("Citation updated");
    }

    @DeleteMapping("/sync/{id}")
    public ResponseEntity<String> deleteCitation(@PathVariable Long id) {
        citationService.deleteCitation(id);
        return ResponseEntity.ok("Citation removed");
    }

    @GetMapping("/paper/{paperId}/citing")
    public ResponseEntity<List<Citation>> getCitationsByCitingPaper(@PathVariable Integer paperId) {
        return ResponseEntity.ok(citationService.getCitationsByCitingPaper(paperId));
    }

    @GetMapping("/paper/{paperId}/cited")
    public ResponseEntity<List<Citation>> getCitationsByCitedPaper(@PathVariable Integer paperId) {
        return ResponseEntity.ok(citationService.getCitationsByCitedPaper(paperId));
    }
}
