package com.ynm.researchpaperservice.Controller;

import com.ynm.researchpaperservice.Model.PaperTag;
import com.ynm.researchpaperservice.service.PaperTagService;
import com.ynm.researchpaperservice.dto.PaperTagDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/paper-tags")
@RequiredArgsConstructor
public class PaperTagController {
    private final PaperTagService paperTagService;

    @PostMapping
    public ResponseEntity<PaperTag> createPaperTag(@RequestBody PaperTagDto request) {
        try {
            PaperTag created = paperTagService.createPaperTag(request);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            String message = e.getMessage();
            if (message.contains("not found")) {
                return ResponseEntity.status(404).build();
            } else if (message.contains("already assigned")) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<PaperTag>> getAllPaperTags() {
        return ResponseEntity.ok(paperTagService.getAllPaperTags());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaperTag> getPaperTagById(@PathVariable Integer id) {
        PaperTag paperTag = paperTagService.getPaperTagById(id);
        return paperTag != null ? ResponseEntity.ok(paperTag) : ResponseEntity.notFound().build();
    }

    @GetMapping("/paper/{paperId}")
    public ResponseEntity<List<PaperTag>> getTagsByPaper(@PathVariable Integer paperId) {
        return ResponseEntity.ok(paperTagService.getTagsByPaperId(paperId));
    }

    @GetMapping("/tag/{tagId}")
    public ResponseEntity<List<PaperTag>> getPapersByTag(@PathVariable Integer tagId) {
        return ResponseEntity.ok(paperTagService.getPapersByTagId(tagId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PaperTag> deletePaperTag(@PathVariable Integer id) {
        try {
            PaperTag deleted = paperTagService.deletePaperTag(id);
            return ResponseEntity.ok(deleted);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
