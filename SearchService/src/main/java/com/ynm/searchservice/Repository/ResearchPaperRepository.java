package com.ynm.searchservice.Repository;

import com.ynm.searchservice.Model.ResearchPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResearchPaperRepository extends JpaRepository<ResearchPaper, Integer> {
    List<ResearchPaper> findByOwnerId(Integer ownerId);
}
