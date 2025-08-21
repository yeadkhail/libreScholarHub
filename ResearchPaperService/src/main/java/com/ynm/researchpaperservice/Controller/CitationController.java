package com.ynm.researchpaperservice.Controller;

import com.ynm.researchpaperservice.Model.Citation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/citations")
@RequiredArgsConstructor
public class CitationController {

//    private final CitationService citationService;

    @PostMapping
    public ResponseEntity<Citation> addCitation(@RequestBody Citation citation) {
//        return ResponseEntity.ok(citationService.addCitation(citation));
        Citation dummy = new Citation();
        dummy.setId(1L);
        dummy.setCitedPaper(null); // no actual paper
        dummy.setCitingPaper(null);
        return ResponseEntity.ok(dummy);
    }

    @GetMapping("/cited/{paperId}")
    public ResponseEntity<List<Citation>> getCitationsOfPaper(@PathVariable Integer paperId) {
//        return ResponseEntity.ok(citationService.getCitationsOfPaper(paperId));
        Citation dummy1 = new Citation();
        dummy1.setId(1L);
        dummy1.setCitedPaper(null);
        dummy1.setCitingPaper(null);

        Citation dummy2 = new Citation();
        dummy2.setId(2L);
        dummy2.setCitedPaper(null);
        dummy2.setCitingPaper(null);

        List<Citation> list = new ArrayList<>();
        list.add(dummy1);
        list.add(dummy2);

        return ResponseEntity.ok(list);
    }

    @GetMapping("/citing/{paperId}")
    public ResponseEntity<List<Citation>> getCitationsByPaper(@PathVariable Integer paperId) {
//        return ResponseEntity.ok(citationService.getCitationsByPaper(paperId));
        Citation dummy1 = new Citation();
        dummy1.setId(3L);
        dummy1.setCitedPaper(null);
        dummy1.setCitingPaper(null);

        Citation dummy2 = new Citation();
        dummy2.setId(4L);
        dummy2.setCitedPaper(null);
        dummy2.setCitingPaper(null);

        List<Citation> list = new ArrayList<>();
        list.add(dummy1);
        list.add(dummy2);

        return ResponseEntity.ok(list);
    }
}
