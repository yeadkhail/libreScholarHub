package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.PaperTag;
import com.ynm.searchservice.Repository.PaperTagRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/papertags")
public class PaperTagController {

    private final PaperTagRepository paperTagRepository;

    public PaperTagController(PaperTagRepository repo) {
        this.paperTagRepository = repo;
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncPaperTag(@RequestBody PaperTag paperTag) {
        paperTagRepository.save(paperTag); // <- real DB insert/update
        return ResponseEntity.ok("PaperTag synced into search DB");
    }

    @DeleteMapping("/sync")
    public ResponseEntity<String> deletePaperTag(@RequestBody PaperTag paperTag) {
        paperTagRepository.delete(paperTag);
        return ResponseEntity.ok("PaperTag removed from search DB");
    }

    @GetMapping("/paper/{paperId}")
    public ResponseEntity<List<PaperTag>> getTagsByPaper(@PathVariable int paperId) {
        return ResponseEntity.ok(paperTagRepository.findByPaperId(paperId));
    }
}
