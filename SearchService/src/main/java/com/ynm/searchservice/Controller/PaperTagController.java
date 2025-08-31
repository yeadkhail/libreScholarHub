package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.PaperTag;
import com.ynm.searchservice.Repository.PaperTagRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/papertags")
public class PaperTagController {

    private final PaperTagRepository paperTagRepository;

    public PaperTagController(PaperTagRepository repo) {
        this.paperTagRepository = repo;
    }

    // 🔹 Sync (insert or update)
    @PostMapping("/sync")
    public ResponseEntity<String> syncPaperTag(@RequestBody PaperTag paperTag) {
        paperTagRepository.save(paperTag);
        return ResponseEntity.ok("PaperTag synced into search DB");
    }

    // 🔹 Delete by ID (not whole object)
    @DeleteMapping("/sync/{id}")
    public ResponseEntity<String> deletePaperTag(@PathVariable Integer id) {
        paperTagRepository.deleteById(id);
        return ResponseEntity.ok("PaperTag removed from search DB");
    }

    // 🔹 Get tags of a specific paper
    @GetMapping("/paper/{paperId}")
    public ResponseEntity<List<PaperTag>> getTagsByPaper(@PathVariable int paperId) {
        return ResponseEntity.ok(paperTagRepository.findByPaperId(paperId));
    }


}
