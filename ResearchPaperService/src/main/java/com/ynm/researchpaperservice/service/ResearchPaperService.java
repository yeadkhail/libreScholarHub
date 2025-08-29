package com.ynm.researchpaperservice.service;


import com.ynm.researchpaperservice.Model.ResearchPaper;
import com.ynm.researchpaperservice.Repository.ResearchPaperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResearchPaperService {
    @Autowired
    private ResearchPaperRepository researchPaperRepository;

    public ResearchPaper saveResearchPaper(ResearchPaper paper) {
        return researchPaperRepository.save(paper);
    }

    public List<ResearchPaper> getAllResearchPapers() {
        return researchPaperRepository.findAll();
    }

    public Optional<ResearchPaper> getResearchPaperById(Integer id) {
        return researchPaperRepository.findById(id);
    }

    public ResearchPaper deleteResearchPaper(Integer id) {
        return researchPaperRepository.findById(id)
                .map(paper -> {
                    researchPaperRepository.delete(paper);
                    return paper;
                })
                .orElseThrow(() -> new RuntimeException("ResearchPaper not found with id " + id));
    }

    public ResearchPaper updateResearchPaper(Integer id, ResearchPaper updatedPaper) {
        return researchPaperRepository.findById(id).map(existingPaper -> {
            if (updatedPaper.getTitle() != null) existingPaper.setTitle(updatedPaper.getTitle());
            if (updatedPaper.getAbstractText() != null) existingPaper.setAbstractText(updatedPaper.getAbstractText());
            if (updatedPaper.getUploadPath() != null) existingPaper.setUploadPath(updatedPaper.getUploadPath());
            if (updatedPaper.getVisibility() != null) existingPaper.setVisibility(updatedPaper.getVisibility());
            if (updatedPaper.getOwnerId() != null) existingPaper.setOwnerId(updatedPaper.getOwnerId());
            if (updatedPaper.getCreatedAt() != null) existingPaper.setCreatedAt(updatedPaper.getCreatedAt());
            if (updatedPaper.getMetric() != null) existingPaper.setMetric(updatedPaper.getMetric());
            return researchPaperRepository.save(existingPaper);
        }).orElseThrow(() -> new RuntimeException("ResearchPaper not found with id " + id));
    }

    public List<ResearchPaper> getResearchPapersByOwner(Integer ownerId) {
        return researchPaperRepository.findByOwnerId(ownerId);
    }

    public List<ResearchPaper> getResearchPapersByVisibility(String visibility) {
        return researchPaperRepository.findByVisibility(visibility);
    }
}
