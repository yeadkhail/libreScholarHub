package com.ynm.researchpaperservice.service;

import com.ynm.researchpaperservice.Model.Citation;
import com.ynm.researchpaperservice.Model.ResearchPaper;
import com.ynm.researchpaperservice.Repository.CitationRepository;
import com.ynm.researchpaperservice.Repository.ResearchPaperRepository;
import com.ynm.researchpaperservice.dto.CitationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CitationService {

    private final CitationRepository citationRepository;
    private final ResearchPaperRepository researchPaperRepository;

    public Citation createCitation(Integer citedPaperId, Integer citingPaperId) {
        Optional<ResearchPaper> cited = researchPaperRepository.findById(citedPaperId);
        Optional<ResearchPaper> citing = researchPaperRepository.findById(citingPaperId);

        if (cited.isEmpty() || citing.isEmpty()) {
            return null;
        }

        Optional<Citation> existing = citationRepository
                .findByCitedPaperIdAndCitingPaperId(citedPaperId, citingPaperId);

        if (existing.isPresent()) {
            return existing.get();
        }

        Citation citation = new Citation();
        citation.setCitedPaper(cited.get());
        citation.setCitingPaper(citing.get());

        return citationRepository.save(citation);
    }

    public Citation updateCitation(Long id, CitationDto dto) {
        return citationRepository.findById(id).map(existing -> {
            ResearchPaper citing = researchPaperRepository.findById(dto.getCitingPaperId())
                    .orElseThrow(() -> new RuntimeException("Invalid citing paper ID"));
            ResearchPaper cited = researchPaperRepository.findById(dto.getCitedPaperId())
                    .orElseThrow(() -> new RuntimeException("Invalid cited paper ID"));

            existing.setCitingPaper(citing);
            existing.setCitedPaper(cited);

            return citationRepository.save(existing);
        }).orElse(null);
    }

    public Citation deleteCitation(Long id) {
        Optional<Citation> existing = citationRepository.findById(id);
        if (existing.isPresent()) {
            Citation citation = existing.get();
            citationRepository.delete(citation);
            return citation;
        } else {
            return null;
        }
    }

    public List<Citation> getCitationsByCitedPaperId(Integer citedPaperId) {
        return citationRepository.findByCitedPaperId(citedPaperId);
    }

    public List<Citation> getCitationsByCitingPaperId(Integer citingPaperId) {
        return citationRepository.findByCitingPaperId(citingPaperId);
    }

    public Citation getCitationById(Long id) {
        return citationRepository.findById(id).orElse(null);
    }
}
