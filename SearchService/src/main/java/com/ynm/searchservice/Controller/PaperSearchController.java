package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.Elastic.ResearchPaperDocument;
import com.ynm.searchservice.service.ResearchPaperSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search/search") // A new base path for all search APIs
@RequiredArgsConstructor
public class PaperSearchController {

    private final ResearchPaperSearchService searchService;

    @GetMapping
    public ResponseEntity<Page<ResearchPaperDocument>> search(
            @RequestParam(name = "query") String query,
            Pageable pageable) {

        Page<ResearchPaperDocument> results = searchService.search(query, pageable);
        return ResponseEntity.ok(results);
    }


    @GetMapping("/by-title")
    public ResponseEntity<List<ResearchPaperDocument>> searchByTitle(
            @RequestParam(name = "title") String title) {

        List<ResearchPaperDocument> results = searchService.searchByTitle(title);
        return ResponseEntity.ok(results);
    }


    @GetMapping("/by-tag")
    public ResponseEntity<List<ResearchPaperDocument>> searchByTag(
            @RequestParam(name = "tag") String tag) {

        List<ResearchPaperDocument> results = searchService.searchByTag(tag);
        return ResponseEntity.ok(results);
    }


    @GetMapping("/all")
    public ResponseEntity<Page<ResearchPaperDocument>> getAllPapers(Pageable pageable) {
        return ResponseEntity.ok(searchService.findAll(pageable));
    }
}