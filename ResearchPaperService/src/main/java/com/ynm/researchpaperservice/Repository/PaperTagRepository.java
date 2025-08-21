package com.ynm.researchpaperservice.Repository;

import com.ynm.researchpaperservice.Model.PaperTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaperTagRepository extends JpaRepository<PaperTag, Integer> {
    List<PaperTag> findByPaperId(Integer paperId);
    List<PaperTag> findByTagId(Integer tagId);
}
