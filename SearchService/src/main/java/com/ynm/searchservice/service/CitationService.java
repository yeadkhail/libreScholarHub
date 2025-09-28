package com.ynm.searchservice.service;

import com.ynm.searchservice.Model.Citation;
import com.ynm.searchservice.Repository.CitationRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CitationService {

    private final CitationRepository citationRepository;

    //@CachePut(value = "citations", key = "#citation.id")
    public Citation syncCitation(Citation citation) {
        return citationRepository.save(citation);
    }


    //@CachePut(value = "citations", key = "#id")
    public Citation updateCitation(Long id, Citation citation) {
        Citation existing = citationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Citation not found"));

        existing.setCitedPaper(citation.getCitedPaper());
        existing.setCitingPaper(citation.getCitingPaper());

        return citationRepository.save(existing);
    }

    //@CacheEvict(value = "citations", key = "#id")
    public void deleteCitation(Long id) {
        if (!citationRepository.existsById(id)) {
            throw new RuntimeException("Citation not found");
        }
        citationRepository.deleteById(id);
    }

    @Cacheable(value = "citationsByCiting", key = "#paperId")
    public List<Citation> getCitationsByCitingPaper(Integer paperId) {
        return citationRepository.findByCitingPaperId(paperId);
    }

    @Cacheable(value = "citationsByCited", key = "#paperId")
    public List<Citation> getCitationsByCitedPaper(Integer paperId) {
        return citationRepository.findByCitedPaperId(paperId);
    }
}
