package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.PaperVersion;
import com.ynm.searchservice.service.PaperVersionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search/paperversions")
@AllArgsConstructor
public class PaperVersionController {

    private final PaperVersionService paperVersionService;

    @PostMapping("/sync")
    public ResponseEntity<String> syncPaperVersion(@RequestBody PaperVersion paperVersion) {
        paperVersionService.syncPaperVersion(paperVersion);
        return ResponseEntity.ok("PaperVersion synced into search DB");
    }

    @DeleteMapping("/sync/{id}")
    public ResponseEntity<String> deletePaperVersion(@PathVariable Integer id) {
        paperVersionService.deletePaperVersion(id);
        return ResponseEntity.ok("PaperVersion removed from search DB");
    }

    @GetMapping("/paper/{paperId}")
    public ResponseEntity<List<PaperVersion>> getVersionsByPaper(@PathVariable Integer paperId) {
        return ResponseEntity.ok(paperVersionService.getVersionsByPaper(paperId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaperVersion> getPaperVersion(@PathVariable Integer id) {
        return ResponseEntity.ok(paperVersionService.getPaperVersion(id));
    }
}
