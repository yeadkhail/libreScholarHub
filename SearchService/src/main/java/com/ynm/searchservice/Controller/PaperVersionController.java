package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.PaperVersion;
import com.ynm.searchservice.Repository.PaperVersionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/paperversions")
public class PaperVersionController {

    private final PaperVersionRepository paperVersionRepository;

    public PaperVersionController(PaperVersionRepository repo) {
        this.paperVersionRepository = repo;
    }

    /**
     * Called by ResearchPaperService when a PaperVersion is created or updated.
     * Performs an upsert in the search DB.
     */
    @PostMapping("/sync")
    public ResponseEntity<String> syncPaperVersion(@RequestBody PaperVersion paperVersion) {
        paperVersionRepository.save(paperVersion); // inserts or updates
        return ResponseEntity.ok("PaperVersion synced into search DB");
    }

    /**
     * Called by ResearchPaperService when a PaperVersion is deleted.
     */
    @DeleteMapping("/sync/{id}")
    public ResponseEntity<String> deletePaperVersion(@PathVariable Integer id) {
        if (paperVersionRepository.existsById(id)) {
            paperVersionRepository.deleteById(id);
            return ResponseEntity.ok("PaperVersion removed from search DB");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Returns all versions of a specific research paper.
     */
    @GetMapping("/paper/{paperId}")
    public ResponseEntity<List<PaperVersion>> getVersionsByPaper(@PathVariable Integer paperId) {
        List<PaperVersion> versions = paperVersionRepository.findByPaperId(paperId);
        return ResponseEntity.ok(versions);
    }

    /**
     * Optional: Get a single PaperVersion by its ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaperVersion> getPaperVersion(@PathVariable Integer id) {
        return ResponseEntity.of(paperVersionRepository.findById(id));
    }
}
