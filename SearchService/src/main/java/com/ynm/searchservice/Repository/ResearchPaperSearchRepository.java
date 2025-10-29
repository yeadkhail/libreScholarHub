package com.ynm.searchservice.Repository; // Or your repository package

import com.ynm.searchservice.Model.Elastic.ResearchPaperDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ResearchPaperSearchRepository extends ElasticsearchRepository<ResearchPaperDocument, Integer> {

    // Spring Data derived queries
    List<ResearchPaperDocument> findByTitleContaining(String title);
    List<ResearchPaperDocument> findByAuthorsContains(String authorName);
    List<ResearchPaperDocument> findByTagsContains(String tag);

    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title\", \"abstractText\", \"authors\", \"tags\"], \"fuzziness\": \"AUTO\"}}")
    Page<ResearchPaperDocument> search(String query, Pageable pageable);
}