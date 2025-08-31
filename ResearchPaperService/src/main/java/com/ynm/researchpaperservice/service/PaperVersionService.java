package com.ynm.researchpaperservice.service;

import com.ynm.researchpaperservice.Model.PaperVersion;
import com.ynm.researchpaperservice.Model.ResearchPaper;
import com.ynm.researchpaperservice.Repository.PaperVersionRepository;
import com.ynm.researchpaperservice.Repository.ResearchPaperRepository;
import com.ynm.researchpaperservice.dto.PaperVersionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class PaperVersionService {

    private final PaperVersionRepository paperVersionRepository;
    private final ResearchPaperRepository researchPaperRepository;
    private final RestTemplate restTemplate;
    private final String searchServiceUrl;

    public PaperVersionService(PaperVersionRepository paperVersionRepository,
                               ResearchPaperRepository researchPaperRepository,
                               RestTemplate restTemplate,
                               @Value("${search.service.url}") String searchServiceUrl) {
        this.paperVersionRepository = paperVersionRepository;
        this.researchPaperRepository = researchPaperRepository;
        this.restTemplate = restTemplate;
        this.searchServiceUrl = searchServiceUrl;
    }

    // CREATE
    public PaperVersion createPaperVersion(Integer paperId, PaperVersionDto dto) {
        ResearchPaper paper = researchPaperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Research paper with id " + paperId + " not found."));

        PaperVersion version = new PaperVersion();
        version.setPaper(paper);
        version.setVersionNumber(dto.getVersionNumber());
        version.setFilePath(dto.getFilePath());
        version.setUploadDate(new Date());

        PaperVersion saved = paperVersionRepository.save(version);

        // Sync with search service
        syncWithSearchService(saved, HttpMethod.POST);

        return saved;
    }

    // UPDATE
    public PaperVersion updatePaperVersion(Integer id, PaperVersionDto dto) {
        PaperVersion existing = paperVersionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PaperVersion with id " + id + " not found."));

        existing.setVersionNumber(dto.getVersionNumber());
        existing.setFilePath(dto.getFilePath());
        existing.setUploadDate(new Date());

        PaperVersion updated = paperVersionRepository.save(existing);

        // Sync with search service
        syncWithSearchService(updated, HttpMethod.POST);

        return updated;
    }

    // DELETE
    public PaperVersion deletePaperVersion(Integer id) {
        PaperVersion existing = paperVersionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PaperVersion with id " + id + " not found."));
        paperVersionRepository.delete(existing);

        // Sync deletion with search service
        syncWithSearchService(existing, HttpMethod.DELETE);

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

    // --- Helper method to sync with search service ---
    private void syncWithSearchService(PaperVersion version, HttpMethod method) {
        try {
            String url = searchServiceUrl + "/paperversions/sync";
            if (method == HttpMethod.DELETE) {
                url += "/" + version.getId();
            }
            log.debug("Syncing PaperVersion with Search Service at: {}", url);

            // Extract the Authorization header
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpHeaders headers = new HttpHeaders();
            if (attrs != null) {
                String bearerToken = attrs.getRequest().getHeader("Authorization");
                if (bearerToken != null && !bearerToken.isEmpty()) {
                    headers.set("Authorization", bearerToken);
                }
            }
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<PaperVersion> entity = method == HttpMethod.POST ? new HttpEntity<>(version, headers)
                    : new HttpEntity<>(headers);

            ResponseEntity<Void> response = restTemplate.exchange(url, method, entity, Void.class);
            log.debug("Search Service sync response status: {}", response.getStatusCode());

        } catch (Exception e) {
            log.error("Failed to sync PaperVersion with Search Service: {}", e.getMessage(), e);
        }
    }
}
