package com.ynm.researchpaperservice.service;

import com.ynm.researchpaperservice.Model.Citation;
import com.ynm.researchpaperservice.Model.ResearchPaper;
import com.ynm.researchpaperservice.Repository.CitationRepository;
import com.ynm.researchpaperservice.Repository.ResearchPaperRepository;
import com.ynm.researchpaperservice.dto.CitationDto;
import com.ynm.researchpaperservice.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CitationService {

    private final CitationRepository citationRepository;
    private final ResearchPaperRepository researchPaperRepository;
    private final RestTemplate restTemplate;
    private final UserDetailsServiceImpl userDetailsService;
    private final JWTService jwtService;

    @Value("${search.service.url}")
    private String searchServiceUrl;

    /** Helper to get current request's Bearer token */
    private String getBearerToken() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            return attrs.getRequest().getHeader("Authorization");
        }
        return null;
    }

    /** Helper to sync citation to Search Service */
    private void syncWithSearchService(String endpoint, HttpMethod method, Object body) {
        try {
            String url = searchServiceUrl + endpoint;
            String bearerToken = getBearerToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (bearerToken != null && !bearerToken.isEmpty()) {
                headers.set("Authorization", bearerToken);
            } else {
                log.warn("No Authorization header found; skipping sync");
                return;
            }

            HttpEntity<Object> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Void> response = restTemplate.exchange(url, method, entity, Void.class);
            log.debug("Search Service sync {} {} status: {}", method, url, response.getStatusCode());
        } catch (Exception e) {
            log.error("Failed to sync citation with Search Service: {}", e.getMessage(), e);
        }
    }

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

        Citation saved = citationRepository.save(citation);

        float lastUpdate = citing.get().getMetric()/1000;
        cited.get().addMetric(lastUpdate);

        citation.setLastUpdate(lastUpdate);

        UserScoreService userScoreService = new UserScoreService(restTemplate);
        String userName = "";

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7);
                userName = jwtService.extractUserName(jwt);

            }
        }
        UserDto user = (UserDto) userDetailsService.loadUserByUsername(userName);
        Long userId = userScoreService.getUserIdByEmail(user.getUsername());
        userScoreService.syncScore(userId,lastUpdate,0f);

        // Sync to Search Service
        syncWithSearchService("/citations/sync", HttpMethod.POST, saved);

        return saved;
    }

//    public Citation updateCitation(Long id, CitationDto dto) {
//        return citationRepository.findById(id).map(existing -> {
//            ResearchPaper citing = researchPaperRepository.findById(dto.getCitingPaperId())
//                    .orElseThrow(() -> new RuntimeException("Invalid citing paper ID"));
//            ResearchPaper cited = researchPaperRepository.findById(dto.getCitedPaperId())
//                    .orElseThrow(() -> new RuntimeException("Invalid cited paper ID"));
//
//            existing.setCitingPaper(citing);
//            existing.setCitedPaper(cited);
//
//            Citation saved = citationRepository.save(existing);
//
//            // Sync update to Search Service
//            syncWithSearchService("/citations/sync/" + id, HttpMethod.PUT, saved);
//
//            return saved;
//        }).orElse(null);
//    }
//
//    public Citation deleteCitation(Long id) {
//        Optional<Citation> existing = citationRepository.findById(id);
//        if (existing.isPresent()) {
//            Citation citation = existing.get();
//            citationRepository.delete(citation);
//
//            // Sync deletion
//            syncWithSearchService("/citations/sync/" + id, HttpMethod.DELETE, null);
//
//            return citation;
//        } else {
//            return null;
//        }
//    }

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
