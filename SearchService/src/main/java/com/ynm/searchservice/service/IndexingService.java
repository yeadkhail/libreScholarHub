package com.ynm.searchservice.service;

import com.ynm.searchservice.Model.Author;
import com.ynm.searchservice.Model.Elastic.ResearchPaperDocument;
import com.ynm.searchservice.Model.PaperTag;
import com.ynm.searchservice.Model.ResearchPaper;
import com.ynm.searchservice.Repository.AuthorRepository; // Assuming this exists
import com.ynm.searchservice.Repository.PaperTagRepository; // Assuming this exists
import com.ynm.searchservice.Repository.ResearchPaperRepository; // Assuming this exists
import com.ynm.searchservice.Repository.ResearchPaperSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndexingService {

    // SQL/JPA Repositories
    private final ResearchPaperRepository researchPaperRepository;
    private final AuthorRepository authorRepository;
    private final PaperTagRepository paperTagRepository;

    // Elasticsearch Repository
    private final ResearchPaperSearchRepository searchRepository;

    /**
     * Re-indexes all papers from the SQL database into Elasticsearch.
     * This is useful for an initial data load or a full rebuild.
     */
    @Transactional(readOnly = true) // Ensures lazy-loaded entities can be accessed
    public void reindexAllPapers() {
        log.info("Starting full re-index of all research papers...");
        searchRepository.deleteAll(); // Clear existing index

        List<ResearchPaper> allPapers = researchPaperRepository.findAll();
        List<ResearchPaperDocument> documents = allPapers.stream()
                .map(this::mapToDocument)
                .collect(Collectors.toList());

        searchRepository.saveAll(documents);
        log.info("Successfully indexed {} papers.", documents.size());
    }

    /**
     * Helper method to convert one JPA ResearchPaper into one ES ResearchPaperDocument.
     * This is where the denormalization happens.
     */
    private ResearchPaperDocument mapToDocument(ResearchPaper paper) {
        // Fetch related authors and get their names
        List<Author> authors = authorRepository.findByPaperId(paper.getId());
        List<String> authorNames = authors.stream()
                .map(author -> author.getUser().getName()) // Assumes User is loaded
                .collect(Collectors.toList());

        // Fetch related tags and get their names
        List<PaperTag> paperTags = paperTagRepository.findByPaperId(paper.getId());
        List<String> tagNames = paperTags.stream()
                .map(paperTag -> paperTag.getTag().getName()) // Assumes Tag is loaded
                .collect(Collectors.toList());

        return ResearchPaperDocument.builder()
                .id(paper.getId())
                .title(paper.getTitle())
                .abstractText(paper.getAbstractText())
                .authors(authorNames)
                .tags(tagNames)
                .createdAt(new java.util.Date(paper.getCreatedAt().getTime())) // Convert sql.Date
                .metric(paper.getMetric())
                .visibility(paper.getVisibility())
                .build();
    }

    /**
     * Call this method from your service whenever a new paper is created
     * or an existing one is updated.
     */
    @Transactional(readOnly = true)
    public void indexSinglePaper(Integer paperId) {
        researchPaperRepository.findById(paperId).ifPresent(paper -> {
            ResearchPaperDocument doc = mapToDocument(paper);
            searchRepository.save(doc);
            log.info("Indexed paper: {}", paperId);
        });
    }

    /**
     * Call this when a paper is deleted.
     */
    public void removePaperFromIndex(Integer paperId) {
        searchRepository.deleteById(paperId);
        log.info("Removed paper from index: {}", paperId);
    }
}