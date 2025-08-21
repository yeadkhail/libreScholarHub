package com.ynm.researchpaperservice.Repository;

import com.ynm.researchpaperservice.Entity.PublisherReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PublisherReviewRepository extends JpaRepository<PublisherReview, Integer> {
    List<PublisherReview> findByPaperId(Integer paperId);
    List<PublisherReview> findByUniPubId(Integer uniPubId);
}

