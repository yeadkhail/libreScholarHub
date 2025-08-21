package com.ynm.searchservice.Repository;

import com.ynm.searchservice.Model.PaperVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaperVersionRepository extends JpaRepository<PaperVersion, Integer> {
    List<PaperVersion> findByPaperId(Integer paperId);
}

