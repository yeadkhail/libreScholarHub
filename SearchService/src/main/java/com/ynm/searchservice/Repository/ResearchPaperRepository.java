package com.ynm.searchservice.Repository;

import com.ynm.searchservice.Model.ResearchPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResearchPaperRepository extends JpaRepository<ResearchPaper, Integer> {

    // 1. For searchByTitle:
    // We add "OrderByMetricDesc" to the method name.
    // "Metric" is your field, and "Desc" means decreasing order.
    List<ResearchPaper> findByTitleContainingIgnoreCaseOrderByMetricDesc(String keyword);

    // 2. For getAllPapers:
    // We create a new method to find all and sort them.
    List<ResearchPaper> findAllByOrderByMetricDesc();

    // 3. For getPapersByTag:
    // (Assuming you used the @Query from our last conversation)
    // We simply add the "ORDER BY pt.paper.metric DESC" clause to the JPQL.
    @Query("SELECT pt.paper FROM PaperTag pt WHERE pt.tag.id = :tagId ORDER BY pt.paper.metric DESC")
    List<ResearchPaper> findPapersByTagId(@Param("tagId") Integer tagId);
}