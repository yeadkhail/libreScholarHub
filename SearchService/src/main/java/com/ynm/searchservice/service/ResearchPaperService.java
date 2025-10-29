package com.ynm.searchservice.service;

import com.ynm.searchservice.Model.PaperTag;
import com.ynm.searchservice.Model.ResearchPaper;
import com.ynm.searchservice.Repository.ResearchPaperRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ResearchPaperService {

    private final ResearchPaperRepository researchPaperRepository;

    //@CachePut(value = "researchPapers", key = "#paper.id")
    public ResearchPaper syncResearchPaper(ResearchPaper paper) {
        return researchPaperRepository.save(paper);
    }

    //@CachePut(value = "researchPapers", key = "#id")
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

    //@CacheEvict(value = "researchPapers", key = "#id")
    public void deleteResearchPaper(Integer id) {
        if (!researchPaperRepository.existsById(id)) {
            throw new RuntimeException("ResearchPaper not found");
        }
        researchPaperRepository.deleteById(id);
    }

    //@Cacheable(value = "researchPapers", key = "#id")
    public ResearchPaper getPaper(Integer id) {
        return researchPaperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ResearchPaper not found"));
    }

    //@Cacheable(value = "papersByTitle", key = "#keyword")
    public List<ResearchPaper> searchByTitle(String keyword) {
        return researchPaperRepository.findByTitleContainingIgnoreCaseOrderByMetricDesc(keyword);
    }

    //@Cacheable(value = "allPapers")
    public List<ResearchPaper> getAllPapers() {
        return researchPaperRepository.findAllByOrderByMetricDesc();
    }
    public List<ResearchPaper> getPapersByTag(int tagId) {
        return researchPaperRepository.findPapersByTagId(tagId);
    }
}
