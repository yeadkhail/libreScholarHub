package com.ynm.researchpaperservice.service;

import com.ynm.researchpaperservice.Model.ResearchPaper;
import com.ynm.researchpaperservice.Repository.ResearchPaperRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ResearchPaperService {

    @Autowired
    private ResearchPaperRepository researchPaperRepository;

    private final RestTemplate restTemplate;
    private final String searchServiceUrl;

    // Folder to save uploaded files
    @Value("${file.upload-dir}")
    private String uploadDir;

    public ResearchPaperService(ResearchPaperRepository researchPaperRepository,
                                RestTemplate restTemplate,
                                @Value("${search.service.url}") String searchServiceUrl) {
        this.researchPaperRepository = researchPaperRepository;
        this.restTemplate = restTemplate;
        this.searchServiceUrl = searchServiceUrl;
    }

    private void syncWithSearchService(String method, String url, ResearchPaper paper) {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            if (attrs != null) {
                String bearerToken = attrs.getRequest().getHeader("Authorization");
                if (bearerToken != null && !bearerToken.isEmpty()) {
                    headers.set("Authorization", bearerToken);
                }
            }

            HttpEntity<ResearchPaper> entity = paper != null ? new HttpEntity<>(paper, headers)
                    : new HttpEntity<>(headers);

            ResponseEntity<Void> response;
            switch (method) {
                case "POST" -> response = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
                case "PUT" -> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
                case "DELETE" -> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
                default -> throw new IllegalArgumentException("Unsupported method: " + method);
            }

            log.debug("SearchService sync {} {} status: {}", method, url, response.getStatusCode());
        } catch (Exception e) {
            log.error("Failed to sync ResearchPaper with SearchService: {}", e.getMessage(), e);
        }
    }

    // Save research paper metadata only
    public ResearchPaper saveResearchPaper(ResearchPaper paper) {
        ResearchPaper saved = researchPaperRepository.save(paper);
        syncWithSearchService("POST", searchServiceUrl + "/research-papers/sync", saved);
        return saved;
    }

    public ResearchPaper saveResearchPaperWithFile(MultipartFile file, ResearchPaper paper) {
        try {
            // Save file on server
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Path.of(uploadDir, filename);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());

            // Set file path in the entity
            paper.setUploadPath(filePath.toString());
            paper.setCreatedAt(new java.sql.Date(System.currentTimeMillis()));

            ResearchPaper saved = researchPaperRepository.save(paper);

            // Sync with search service
            syncWithSearchService("POST", searchServiceUrl + "/research-papers/sync", saved);

            return saved;
        } catch (Exception e) {
            log.error("Failed to save research paper file", e);
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
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

            ResearchPaper saved = researchPaperRepository.save(existingPaper);
            syncWithSearchService("PUT", searchServiceUrl + "/research-papers/sync/" + id, saved);
            return saved;
        }).orElseThrow(() -> new RuntimeException("ResearchPaper not found with id " + id));
    }

    public ResearchPaper deleteResearchPaper(Integer id) {
        return researchPaperRepository.findById(id).map(paper -> {
            researchPaperRepository.delete(paper);
            syncWithSearchService("DELETE", searchServiceUrl + "/research-papers/sync/" + id, null);
            return paper;
        }).orElseThrow(() -> new RuntimeException("ResearchPaper not found with id " + id));
    }

    public List<ResearchPaper> getAllResearchPapers() {
        return researchPaperRepository.findAll();
    }

    public Optional<ResearchPaper> getResearchPaperById(Integer id) {
        return researchPaperRepository.findById(id);
    }

    public List<ResearchPaper> getResearchPapersByOwner(Integer ownerId) {
        return researchPaperRepository.findByOwnerId(ownerId);
    }

    public List<ResearchPaper> getResearchPapersByVisibility(String visibility) {
        return researchPaperRepository.findByVisibility(visibility);
    }
}
