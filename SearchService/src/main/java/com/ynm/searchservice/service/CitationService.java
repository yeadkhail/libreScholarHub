package com.ynm.searchservice.service;

import com.ynm.searchservice.Model.Citation;
import com.ynm.searchservice.Repository.CitationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CitationService {

    private final CitationRepository citationRepository;

    public Citation syncCitation(Citation citation) {
        return citationRepository.save(citation);
    }

    public Citation updateCitation(Long id, Citation citation) {
        Citation existing = citationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Citation not found"));

        existing.setCitedPaper(citation.getCitedPaper());
        existing.setCitingPaper(citation.getCitingPaper());

        return citationRepository.save(existing);
    }

    public void deleteCitation(Long id) {
        if (!citationRepository.existsById(id)) {
            throw new RuntimeException("Citation not found");
        }
        citationRepository.deleteById(id);
    }

    public List<Citation> getCitationsByCitingPaper(Integer paperId) {
        return citationRepository.findByCitingPaperId(paperId);
    }

    public List<Citation> getCitationsByCitedPaper(Integer paperId) {
        return citationRepository.findByCitedPaperId(paperId);
    }
}
