package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.*;
import com.ynm.searchservice.Repository.*;
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

    @PostMapping("/sync")
    public ResponseEntity<String> syncPaperVersion(@RequestBody PaperVersion pv) {
        paperVersionRepository.save(pv);
        return ResponseEntity.ok("PaperVersion synced");
    }

    @DeleteMapping("/sync/{id}")
    public ResponseEntity<String> deletePaperVersion(@PathVariable Integer id) {
        paperVersionRepository.deleteById(id);
        return ResponseEntity.ok("PaperVersion removed");
    }

    @GetMapping("/paper/{paperId}")
    public ResponseEntity<List<PaperVersion>> getVersionsByPaper(@PathVariable Integer paperId) {
        return ResponseEntity.ok(paperVersionRepository.findByPaperId(paperId));
    }
}
