package com.ynm.searchservice.Repository;

import com.ynm.searchservice.Model.Citation;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitationRepository extends JpaRepository<Citation, Long> {
    List<Citation> findByCitedPaperId(Integer citedPaperId);
    List<Citation> findByCitingPaperId(Integer citingPaperId);

    List<Citation> findByCitedPaperId(Integer citedPaperId, Sort sort);
}
