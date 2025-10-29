package com.ynm.researchpaperservice.service;

import com.ynm.researchpaperservice.Model.Author;
import com.ynm.researchpaperservice.Model.Citation;
import com.ynm.researchpaperservice.Model.ResearchPaper;
import com.ynm.researchpaperservice.Repository.CitationRepository;
import com.ynm.researchpaperservice.Repository.ResearchPaperRepository;
import com.ynm.researchpaperservice.dto.CitationDto;
import com.ynm.researchpaperservice.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CitationService {

    private final CitationRepository citationRepository;
    private final ResearchPaperRepository researchPaperRepository;
    private final RestTemplate restTemplate;
    private final UserDetailsServiceImpl userDetailsService;
    private final JWTService jwtService;
    private final RabbitTemplate rabbitTemplate; // 2. Add RabbitTemplate to final fields
    private final AuthorService authorService;   // 3. Add AuthorService (to get author names)
    private final ResearchPaperService researchPaperService;

    @Value("${search.service.url}")
    private String searchServiceUrl;
    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;
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
        // 1. Fetch the actual ResearchPaper objects, or throw an exception if not found
        ResearchPaper citedPaper = researchPaperRepository.findById(citedPaperId)
                .orElseThrow(() -> new RuntimeException("Cited paper not found with id: " + citedPaperId));
        ResearchPaper citingPaper = researchPaperRepository.findById(citingPaperId)
                .orElseThrow(() -> new RuntimeException("Citing paper not found with id: " + citingPaperId));

        // 2. Check if this citation already exists
        Optional<Citation> existing = citationRepository
                .findByCitedPaperIdAndCitingPaperId(citedPaperId, citingPaperId);

        if (existing.isPresent()) {
            return existing.get(); // Return the existing one
        }

        // 3. Create the new citation using the paper objects
        Citation citation = new Citation();
        citation.setCitedPaper(citedPaper);
        citation.setCitingPaper(citingPaper);

        Citation saved = citationRepository.save(citation);

        // 4. Update metrics using the paper objects
        float increaseMetric = citingPaper.getMetric() / 1000;
//        citedPaper.addMetric(increaseMetric);
        researchPaperService.updatePaperMetric(citedPaperId,increaseMetric,0F);
        citation.setLastUpdate(increaseMetric);

        // 5. Sync user score (your existing logic)
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
        userScoreService.syncScore(userId, increaseMetric, 0f);

        // 6. Sync to Search Service
        syncWithSearchService("/citations/sync", HttpMethod.POST, saved);

        // 7. Send RabbitMQ notification (now using the unwrapped paper objects)
        try {
            List<Author> citedAuthors = authorService.getAuthorsByPaper(citedPaper.getId());

            String authorIdentifiers = citedAuthors.stream()
                    .map(author -> author.getUserId() != null ? "User " + author.getUserId() : "an author")
                    .collect(Collectors.joining(", "));
             Long ownerId = citedPaper.getOwnerId();

            String message = String.format(
                    "Your paper '%s' (ID: %d) was cited by '%s' (ID: %d). Congratulations to: %s. (OwnerID: %d)",
                    citedPaper.getTitle(),
                    citedPaper.getId(),
                    citingPaper.getTitle(),
                    citingPaper.getId(),
                    authorIdentifiers,
                    ownerId // Send the ID directly
            );
            log.info("Sending message to RabbitMQ: {}", message);
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
        } catch (Exception e) {
            log.error("Failed to send RabbitMQ message for new citation: {}", e.getMessage());
        }

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
