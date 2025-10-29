package com.ynm.searchservice.Repository;

import com.ynm.searchservice.Model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByPaperId(Integer paperId);

    // 1. CORRECTED: Renamed to "OrderByScoreDesc"
    List<Review> findByPaperIdOrderByScoreDesc(Integer paperId);

    // 2. CORRECTED: Renamed to "OrderByScoreAsc" (Asc is default, but explicit is clearer)
    List<Review> findByPaperIdOrderByScoreAsc(Integer paperId);

    List<Review> findByUserId(Integer userId);
}