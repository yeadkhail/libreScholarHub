package com.ynm.searchservice.service;

import com.ynm.searchservice.Model.PaperVersion;
import com.ynm.searchservice.Repository.PaperVersionRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PaperVersionService {

    private final PaperVersionRepository paperVersionRepository;

    //@CachePut(value = "paperVersions", key = "#paperVersion.id")
    public PaperVersion syncPaperVersion(PaperVersion paperVersion) {
        return paperVersionRepository.save(paperVersion); // insert or update
    }

    //@CacheEvict(value = "paperVersions", key = "#id")
    public void deletePaperVersion(Integer id) {
        if (!paperVersionRepository.existsById(id)) {
            throw new RuntimeException("PaperVersion not found");
        }
        paperVersionRepository.deleteById(id);
    }

    //@Cacheable(value = "versionsByPaper", key = "#paperId")
    public List<PaperVersion> getVersionsByPaper(Integer paperId) {
        return paperVersionRepository.findByPaperId(paperId);
    }

    //@Cacheable(value = "paperVersions", key = "#id")
    public PaperVersion getPaperVersion(Integer id) {
        return paperVersionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PaperVersion not found"));
    }
}
