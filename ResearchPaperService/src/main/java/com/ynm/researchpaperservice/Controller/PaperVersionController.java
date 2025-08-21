package com.ynm.researchpaperservice.Controller;

import com.ynm.researchpaperservice.Model.PaperVersion;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

@RestController
@RequestMapping("/papers/{paperId}/versions")
@RequiredArgsConstructor
public class PaperVersionController {

//    private final PaperVersionService versionService;

    @PostMapping
    public ResponseEntity<PaperVersion> uploadVersion(@PathVariable Integer paperId, @RequestBody PaperVersion version) {
//        return ResponseEntity.ok(versionService.addVersion(paperId, version));
        PaperVersion dummy = new PaperVersion();
        dummy.setId(1);
        dummy.setPaper(null); // no actual paper
        dummy.setVersionNumber(version.getVersionNumber() != null ? version.getVersionNumber() : 1);
        dummy.setFilePath(version.getFilePath() != null ? version.getFilePath() : "dummy/path/file.pdf");
        dummy.setUploadDate(new Date());
        return ResponseEntity.ok(dummy);
    }

    @GetMapping
    public ResponseEntity<List<PaperVersion>> getVersions(@PathVariable Integer paperId) {
//        return ResponseEntity.ok(versionService.getVersionsByPaper(paperId));
        PaperVersion dummy1 = new PaperVersion();
        dummy1.setId(1);
        dummy1.setPaper(null);
        dummy1.setVersionNumber(1);
        dummy1.setFilePath("dummy/path/file_v1.pdf");
        dummy1.setUploadDate(new Date());

        PaperVersion dummy2 = new PaperVersion();
        dummy2.setId(2);
        dummy2.setPaper(null);
        dummy2.setVersionNumber(2);
        dummy2.setFilePath("dummy/path/file_v2.pdf");
        dummy2.setUploadDate(new Date());

        List<PaperVersion> list = new ArrayList<>();
        list.add(dummy1);
        list.add(dummy2);

        return ResponseEntity.ok(list);
    }
}
