package com.ynm.researchpaperservice.Repository;

import com.ynm.researchpaperservice.Model.ResearchPaper;
import com.ynm.researchpaperservice.Model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByPaperId(Integer paperId);
    List<Review> findByUserId(Integer userId);
    Optional<Review> findByUserIdAndPaper(Long userId, ResearchPaper paper);
}
