package com.ynm.searchservice.service;

import com.ynm.searchservice.Model.PaperVersion;
import com.ynm.searchservice.Repository.PaperVersionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PaperVersionService {

    private final PaperVersionRepository paperVersionRepository;

    public PaperVersion syncPaperVersion(PaperVersion paperVersion) {
        return paperVersionRepository.save(paperVersion); // insert or update
    }

    public void deletePaperVersion(Integer id) {
        if (!paperVersionRepository.existsById(id)) {
            throw new RuntimeException("PaperVersion not found");
        }
        paperVersionRepository.deleteById(id);
    }

    public List<PaperVersion> getVersionsByPaper(Integer paperId) {
        return paperVersionRepository.findByPaperId(paperId);
    }

    public PaperVersion getPaperVersion(Integer id) {
        return paperVersionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PaperVersion not found"));
    }
}
