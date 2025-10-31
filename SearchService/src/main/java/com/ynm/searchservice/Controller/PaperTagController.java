package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.PaperTag;
import com.ynm.searchservice.service.PaperTagService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search/papertags")
@AllArgsConstructor
public class PaperTagController {

    private final PaperTagService paperTagService;

    @PostMapping("/sync")
    public ResponseEntity<String> syncPaperTag(@RequestBody PaperTag paperTag) {
        paperTagService.syncPaperTag(paperTag);
        return ResponseEntity.ok("PaperTag synced into search DB");
    }

    @DeleteMapping("/sync/{id}")
    public ResponseEntity<String> deletePaperTag(@PathVariable Integer id) {
        paperTagService.deletePaperTag(id);
        return ResponseEntity.ok("PaperTag removed from search DB");
    }

    @GetMapping("/paper/{paperId}")
    public ResponseEntity<List<PaperTag>> getTagsByPaper(@PathVariable int paperId) {
        return ResponseEntity.ok(paperTagService.getTagsByPaper(paperId));
    }
}
