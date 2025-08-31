package com.ynm.researchpaperservice.service;

import com.ynm.researchpaperservice.Model.PaperTag;
import com.ynm.researchpaperservice.Repository.PaperTagRepository;
import com.ynm.researchpaperservice.Repository.ResearchPaperRepository;
import com.ynm.researchpaperservice.Repository.TagRepository;
import com.ynm.researchpaperservice.dto.PaperTagDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaperTagService {

    private final PaperTagRepository paperTagRepository;
    private final ResearchPaperRepository researchPaperRepository;
    private final TagRepository tagRepository;
    private final RestTemplate restTemplate;

    @Value("${search.service.url}")
    private String searchServiceUrl;

    public PaperTag createPaperTag(PaperTagDto request) {
        Integer paperId = request.getPaperId();
        Integer tagId = request.getTagId();

        var paper = researchPaperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Paper not found with id " + paperId));

        var tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found with id " + tagId));

        if (paperTagRepository.existsByPaperIdAndTagId(paperId, tagId)) {
            throw new RuntimeException("This tag is already assigned to the paper.");
        }

        PaperTag paperTag = new PaperTag();
        paperTag.setPaper(paper);
        paperTag.setTag(tag);

        PaperTag saved = paperTagRepository.save(paperTag);

        // Sync to SearchService
        try {
            String url = searchServiceUrl + "/paper-tags/sync";
            log.debug("Syncing PaperTag to Search Service at: {}", url);

            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                String bearerToken = attrs.getRequest().getHeader("Authorization");
                if (bearerToken != null && !bearerToken.isEmpty()) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Authorization", bearerToken);
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    HttpEntity<PaperTag> entity = new HttpEntity<>(saved, headers);

                    ResponseEntity<Void> response = restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            entity,
                            Void.class
                    );

                    log.debug("Search Service sync response status: {}", response.getStatusCode());
                } else {
                    log.warn("No Authorization header found in the incoming request; skipping sync");
                }
            }
        } catch (Exception e) {
            log.error("Failed to sync PaperTag with Search Service: {}", e.getMessage(), e);
        }

        return saved;
    }

    public List<PaperTag> getAllPaperTags() {
        return paperTagRepository.findAll();
    }

    public PaperTag getPaperTagById(Integer id) {
        return paperTagRepository.findById(id).orElse(null);
    }

    public List<PaperTag> getTagsByPaperId(Integer paperId) {
        return paperTagRepository.findByPaperId(paperId);
    }

    public List<PaperTag> getPapersByTagId(Integer tagId) {
        return paperTagRepository.findByTagId(tagId);
    }

    public PaperTag deletePaperTag(Integer id) {
        PaperTag tag = paperTagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PaperTag not found with id " + id));

        paperTagRepository.delete(tag);

        // Sync deletion with SearchService
        try {
            String url = searchServiceUrl + "/paper-tags/sync/" + id;
            log.debug("Calling Search Service DELETE at: {}", url);

            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                String bearerToken = attrs.getRequest().getHeader("Authorization");
                if (bearerToken != null && !bearerToken.isEmpty()) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Authorization", bearerToken);

                    HttpEntity<Void> entity = new HttpEntity<>(headers);

                    ResponseEntity<Void> response = restTemplate.exchange(
                            url,
                            HttpMethod.DELETE,
                            entity,
                            Void.class
                    );

                    log.debug("Search Service DELETE response status: {}", response.getStatusCode());
                } else {
                    log.warn("No Authorization header found in the incoming request");
                }
            }
        } catch (Exception e) {
            log.error("Failed to delete PaperTag in Search Service: {}", e.getMessage(), e);
        }

        return tag;
    }
}
