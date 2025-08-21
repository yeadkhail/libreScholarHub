package com.ynm.researchpaperservice.Controller;

import com.ynm.researchpaperservice.Entity.PaperVersion;
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
        dummy.setVersion_number(version.getVersion_number() != null ? version.getVersion_number() : 1);
        dummy.setFile_path(version.getFile_path() != null ? version.getFile_path() : "dummy/path/file.pdf");
        dummy.setUpload_date(new Date());
        return ResponseEntity.ok(dummy);
    }

    @GetMapping
    public ResponseEntity<List<PaperVersion>> getVersions(@PathVariable Integer paperId) {
//        return ResponseEntity.ok(versionService.getVersionsByPaper(paperId));
        PaperVersion dummy1 = new PaperVersion();
        dummy1.setId(1);
        dummy1.setPaper(null);
        dummy1.setVersion_number(1);
        dummy1.setFile_path("dummy/path/file_v1.pdf");
        dummy1.setUpload_date(new Date());

        PaperVersion dummy2 = new PaperVersion();
        dummy2.setId(2);
        dummy2.setPaper(null);
        dummy2.setVersion_number(2);
        dummy2.setFile_path("dummy/path/file_v2.pdf");
        dummy2.setUpload_date(new Date());

        List<PaperVersion> list = new ArrayList<>();
        list.add(dummy1);
        list.add(dummy2);

        return ResponseEntity.ok(list);
    }
}
