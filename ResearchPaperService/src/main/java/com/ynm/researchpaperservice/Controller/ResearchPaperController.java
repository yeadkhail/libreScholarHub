package com.ynm.researchpaperservice.Controller;
import com.ynm.researchpaperservice.Model.ResearchPaper;
import com.ynm.researchpaperservice.service.ResearchPaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/research-paper/paper")
@RequiredArgsConstructor
public class ResearchPaperController {

    @Autowired
    private ResearchPaperService researchPaperService;

    // ✅ Create new research paper
    @PostMapping
    public ResponseEntity<ResearchPaper> createResearchPaper(@RequestBody ResearchPaper paper) {
        return ResponseEntity.ok(researchPaperService.saveResearchPaper(paper));
    }
    @PostMapping(value = "/upload")
    public ResponseEntity<ResearchPaper> uploadResearchPaper(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("abstractText") String abstractText,
            @RequestParam("visibility") String visibility
            //@RequestParam("metric") Integer metric
    ) {
        ResearchPaper paper = new ResearchPaper();
        paper.setTitle(title);
        paper.setAbstractText(abstractText);
        paper.setVisibility(visibility);
        paper.setMetric(0f);

        ResearchPaper saved = researchPaperService.saveResearchPaperWithFile(file, paper);
        return ResponseEntity.ok(saved);
    }

    // ✅ Get all research papers
    @GetMapping
    public ResponseEntity<List<ResearchPaper>> getAllResearchPapers() {
        return ResponseEntity.ok(researchPaperService.getAllResearchPapers());
    }

    // ✅ Get research paper by ID
    @GetMapping("/{id}")
    public ResponseEntity<ResearchPaper> getResearchPaperById(@PathVariable Integer id) {
        Optional<ResearchPaper> paper = researchPaperService.getResearchPaperById(id);
        return paper.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Update research paper
    @PutMapping("/{id}")
    public ResponseEntity<ResearchPaper> updateResearchPaper(@PathVariable Integer id,
                                                             @RequestBody ResearchPaper updatedPaper) {
        try {
            return ResponseEntity.ok(researchPaperService.updateResearchPaper(id, updatedPaper));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Delete research paper
    @DeleteMapping("/{id}")
    public ResponseEntity<ResearchPaper> deleteResearchPaper(@PathVariable Integer id) {
        try {
            ResearchPaper deletedPaper = researchPaperService.deleteResearchPaper(id);
            return ResponseEntity.ok(deletedPaper); // 200 with deleted paper in body
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // 404 if not found
        }
    }

    // ✅ Get research papers by owner
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<ResearchPaper>> getResearchPapersByOwner(@PathVariable Integer ownerId) {
        return ResponseEntity.ok(researchPaperService.getResearchPapersByOwner(ownerId));
    }

    // ✅ Get research papers by visibility
    @GetMapping("/visibility/{visibility}")
    public ResponseEntity<List<ResearchPaper>> getResearchPapersByVisibility(@PathVariable String visibility) {
        return ResponseEntity.ok(researchPaperService.getResearchPapersByVisibility(visibility));
    }
}
