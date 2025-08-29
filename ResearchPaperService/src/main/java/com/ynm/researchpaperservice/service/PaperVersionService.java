package com.ynm.researchpaperservice.service;

import com.ynm.researchpaperservice.Model.PaperVersion;
import com.ynm.researchpaperservice.Model.ResearchPaper;
import com.ynm.researchpaperservice.Repository.PaperVersionRepository;
import com.ynm.researchpaperservice.Repository.ResearchPaperRepository;
import com.ynm.researchpaperservice.dto.PaperVersionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaperVersionService {

    private final PaperVersionRepository paperVersionRepository;
    private final ResearchPaperRepository researchPaperRepository;

    // CREATE
    public PaperVersion createPaperVersion(Integer paperId, PaperVersionDto dto) {
        ResearchPaper paper = researchPaperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Research paper with id " + paperId + " not found."));

        PaperVersion version = new PaperVersion();
        version.setPaper(paper);
        version.setVersionNumber(dto.getVersionNumber());
        version.setFilePath(dto.getFilePath());
        version.setUploadDate(new Date());

        return paperVersionRepository.save(version);
    }

    // UPDATE
    public PaperVersion updatePaperVersion(Integer id, PaperVersionDto dto) {
        PaperVersion existing = paperVersionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PaperVersion with id " + id + " not found."));

        existing.setVersionNumber(dto.getVersionNumber());
        existing.setFilePath(dto.getFilePath());
        existing.setUploadDate(new Date());

        return paperVersionRepository.save(existing);
    }

    // DELETE
    public PaperVersion deletePaperVersion(Integer id) {
        PaperVersion existing = paperVersionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PaperVersion with id " + id + " not found."));
        paperVersionRepository.delete(existing);
        return existing;
    }

    // GET by ID
    public PaperVersion getPaperVersionById(Integer id) {
        return paperVersionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PaperVersion with id " + id + " not found."));
    }

    // GET all
    public List<PaperVersion> getAllPaperVersions() {
        return paperVersionRepository.findAll();
    }
}
