package com.ynm.researchpaperservice.Repository;

import com.ynm.researchpaperservice.Model.ResearchPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResearchPaperRepository extends JpaRepository<ResearchPaper, Integer> {
    List<ResearchPaper> findByOwnerId(Integer ownerId);
    List<ResearchPaper> findByVisibility(String visibility);
}