package com.ynm.searchservice.Repository;

import com.ynm.searchservice.Model.Author;
import com.ynm.searchservice.Model.ResearchPaper;
import com.ynm.searchservice.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {
    List<Author> findByPaperId(Integer paperId);
    List<Author> findByUserId(Integer userId);

    // New method: find author by both user and paper
    Optional<Author> findByUserIdAndPaperId(Integer userId, Integer paperId);

}
