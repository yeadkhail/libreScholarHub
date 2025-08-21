package com.ynm.researchpaperservice.Repository;

import com.ynm.researchpaperservice.Model.Author;
import com.ynm.researchpaperservice.Model.ResearchPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {

    // Find all authors for a specific research paper
    List<Author> findByPaper(ResearchPaper paper);

    // Find all author entries for a specific user
    List<Author> findByUserId(Integer userId);
}
