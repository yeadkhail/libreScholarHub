package com.ynm.searchservice.service;

import com.ynm.searchservice.Model.Elastic.ResearchPaperDocument;
import com.ynm.searchservice.Repository.ResearchPaperSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResearchPaperSearchService {

    private final ResearchPaperSearchRepository searchRepository;


    public Page<ResearchPaperDocument> search(String query, Pageable pageable) {
        return searchRepository.search(query, pageable);
    }


    public List<ResearchPaperDocument> searchByTitle(String title) {
        // Note: We don't need @Cacheable. Elasticsearch is already a cache.
        return searchRepository.findByTitleContaining(title);
    }


    public List<ResearchPaperDocument> searchByTag(String tag) {
        return searchRepository.findByTagsContains(tag);
    }


    public Page<ResearchPaperDocument> findAll(Pageable pageable) {
        return searchRepository.findAll(pageable);
    }
}