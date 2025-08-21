package com.ynm.researchpaperservice.Controller;

import com.ynm.researchpaperservice.Entity.ResearchPaper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

@RestController
@RequestMapping("/papers")
@RequiredArgsConstructor
public class ResearchPaperController {

//    private final ResearchPaperService paperService;

    @PostMapping
    public ResponseEntity<ResearchPaper> uploadPaper(@RequestBody ResearchPaper paper) {
//        return ResponseEntity.ok(paperService.uploadPaper(paper));
        ResearchPaper dummy = new ResearchPaper();
        dummy.setId(1);
        dummy.setTitle(paper.getTitle() != null ? paper.getTitle() : "Dummy Title");
        dummy.setAbstractText(paper.getAbstractText() != null ? paper.getAbstractText() : "Dummy Abstract");
        dummy.setUploadPath(paper.getUploadPath() != null ? paper.getUploadPath() : "dummy/path/file.pdf");
        dummy.setVisibility(paper.getVisibility() != null ? paper.getVisibility() : "PUBLIC");
        dummy.setOwnerId(paper.getOwnerId() != null ? paper.getOwnerId() : 1001);
        dummy.setCreatedAt((java.sql.Date) new Date());
        dummy.setMetric(paper.getMetric() != null ? paper.getMetric() : 0);
        return ResponseEntity.ok(dummy);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResearchPaper> getPaper(@PathVariable Integer id) {
//        return ResponseEntity.ok(paperService.getPaper(id));
        ResearchPaper dummy = new ResearchPaper();
        dummy.setId(id);
        dummy.setTitle("Dummy Paper " + id);
        dummy.setAbstractText("Dummy Abstract " + id);
        dummy.setUploadPath("dummy/path/file_" + id + ".pdf");
        dummy.setVisibility("PUBLIC");
        dummy.setOwnerId(1001);
        dummy.setCreatedAt((java.sql.Date) new Date());
        dummy.setMetric(0);
        return ResponseEntity.ok(dummy);
    }

    @GetMapping
    public ResponseEntity<List<ResearchPaper>> getAllPapers() {
//        return ResponseEntity.ok(paperService.getAllPapers());
        List<ResearchPaper> list = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            ResearchPaper dummy = new ResearchPaper();
            dummy.setId(i);
            dummy.setTitle("Dummy Paper " + i);
            dummy.setAbstractText("Dummy Abstract " + i);
            dummy.setUploadPath("dummy/path/file_" + i + ".pdf");
            dummy.setVisibility("PUBLIC");
            dummy.setOwnerId(1000 + i);
            dummy.setCreatedAt((java.sql.Date) new Date());
            dummy.setMetric(0);
            list.add(dummy);
        }
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResearchPaper> updatePaper(@PathVariable Integer id, @RequestBody ResearchPaper paper) {
//        return ResponseEntity.ok(paperService.updatePaper(id, paper));
        ResearchPaper dummy = new ResearchPaper();
        dummy.setId(id);
        dummy.setTitle(paper.getTitle() != null ? paper.getTitle() : "Updated Dummy Title");
        dummy.setAbstractText(paper.getAbstractText() != null ? paper.getAbstractText() : "Updated Dummy Abstract");
        dummy.setUploadPath(paper.getUploadPath() != null ? paper.getUploadPath() : "dummy/path/updated_file.pdf");
        dummy.setVisibility(paper.getVisibility() != null ? paper.getVisibility() : "PUBLIC");
        dummy.setOwnerId(paper.getOwnerId() != null ? paper.getOwnerId() : 1001);
        dummy.setCreatedAt((java.sql.Date) new Date());
        dummy.setMetric(paper.getMetric() != null ? paper.getMetric() : 0);
        return ResponseEntity.ok(dummy);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePaper(@PathVariable Integer id) {
//        paperService.deletePaper(id);
//        return ResponseEntity.noContent().build();
        return ResponseEntity.ok("Called deletePaper for paperId=" + id);
    }
}
