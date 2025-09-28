package com.ynm.searchservice.service;

import com.ynm.searchservice.Model.ResearchPaper;
import com.ynm.searchservice.Repository.ResearchPaperRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ResearchPaperService {

    private final ResearchPaperRepository researchPaperRepository;

    public ResearchPaper syncResearchPaper(ResearchPaper paper) {
        return researchPaperRepository.save(paper);
    }

    public ResearchPaper updateResearchPaper(Integer id, ResearchPaper paper) {
        return researchPaperRepository.findById(id).map(existing -> {
            if (paper.getTitle() != null) existing.setTitle(paper.getTitle());
            if (paper.getAbstractText() != null) existing.setAbstractText(paper.getAbstractText());
            if (paper.getUploadPath() != null) existing.setUploadPath(paper.getUploadPath());
            if (paper.getVisibility() != null) existing.setVisibility(paper.getVisibility());
            if (paper.getOwnerId() != null) existing.setOwnerId(paper.getOwnerId());
            if (paper.getCreatedAt() != null) existing.setCreatedAt(paper.getCreatedAt());
            if (paper.getMetric() != null) existing.setMetric(paper.getMetric());
            return researchPaperRepository.save(existing);
        }).orElseGet(() -> researchPaperRepository.save(paper));
    }

    public void deleteResearchPaper(Integer id) {
        if (!researchPaperRepository.existsById(id)) {
            throw new RuntimeException("ResearchPaper not found");
        }
        researchPaperRepository.deleteById(id);
    }

    public ResearchPaper getPaper(Integer id) {
        return researchPaperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ResearchPaper not found"));
    }

    public List<ResearchPaper> searchByTitle(String keyword) {
        return researchPaperRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public List<ResearchPaper> getAllPapers() {
        return researchPaperRepository.findAll();
    }
}
