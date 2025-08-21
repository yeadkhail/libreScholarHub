package com.ynm.researchpaperservice.Repository;

import com.ynm.researchpaperservice.Model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {
    List<Author> findByPaperId(Integer paperId);
    List<Author> findByUserId(Integer userId);
}
