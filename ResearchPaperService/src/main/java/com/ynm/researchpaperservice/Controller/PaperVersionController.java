package com.ynm.researchpaperservice.Controller;

import com.ynm.researchpaperservice.Model.PaperVersion;
import com.ynm.researchpaperservice.service.PaperVersionService;
import com.ynm.researchpaperservice.dto.PaperVersionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paper-versions")
@RequiredArgsConstructor
public class PaperVersionController {

    private final PaperVersionService paperVersionService;

    // CREATE
    @PostMapping("/{paperId}")
    public ResponseEntity<PaperVersion> createPaperVersion(@PathVariable Integer paperId,
                                                           @RequestBody PaperVersionDto dto) {
        try {
            return ResponseEntity.ok(paperVersionService.createPaperVersion(paperId, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<PaperVersion> updatePaperVersion(@PathVariable Integer id,
                                                           @RequestBody PaperVersionDto dto) {
        try {
            return ResponseEntity.ok(paperVersionService.updatePaperVersion(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }

    // DELETE (return deleted version)
    @DeleteMapping("/{id}")
    public ResponseEntity<PaperVersion> deletePaperVersion(@PathVariable Integer id) {
        try {
            PaperVersion deleted = paperVersionService.deletePaperVersion(id);
            return ResponseEntity.ok(deleted);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }

    // GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<PaperVersion> getPaperVersionById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(paperVersionService.getPaperVersionById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }

    // GET all
    @GetMapping
    public ResponseEntity<List<PaperVersion>> getAllPaperVersions() {
        return ResponseEntity.ok(paperVersionService.getAllPaperVersions());
    }
}
