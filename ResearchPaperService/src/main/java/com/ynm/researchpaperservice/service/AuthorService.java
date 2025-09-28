package com.ynm.researchpaperservice.service;

import com.ynm.researchpaperservice.Model.Author;
import com.ynm.researchpaperservice.Model.ResearchPaper;
import com.ynm.researchpaperservice.Repository.AuthorRepository;
import com.ynm.researchpaperservice.Repository.ResearchPaperRepository;
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
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final ResearchPaperRepository paperRepository;
    private final RestTemplate restTemplate;
    private final String searchServiceUrl;

    public AuthorService(AuthorRepository authorRepository,
                         ResearchPaperRepository paperRepository,
                         RestTemplate restTemplate,
                         @Value("${search.service.url}") String searchServiceUrl) {
        this.authorRepository = authorRepository;
        this.paperRepository = paperRepository;
        this.restTemplate = restTemplate;
        this.searchServiceUrl = searchServiceUrl;
    }
    /**
     * Add an author to a paper
     */

    public Author addAuthor(Integer paperId, Author author) {
        // Fetch the paper
        ResearchPaper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Paper not found"));
        author.setPaper(paper);

        // Save the author locally
        Author savedAuthor = authorRepository.save(author);
        // Sync with Search Service
        try {
            String url = searchServiceUrl + "/authors/sync";
            log.debug("Syncing author to Search Service at: {}", url);

            // Extract the Authorization header from the incoming request
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                String bearerToken = attrs.getRequest().getHeader("Authorization");
                if (bearerToken != null && !bearerToken.isEmpty()) {
                    System.out.println(bearerToken);
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Authorization", bearerToken); // propagate JWT
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    HttpEntity<Author> entity = new HttpEntity<>(savedAuthor, headers);

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
            } else {
                log.warn("No request attributes available; skipping token propagation");
            }
        } catch (Exception e) {
            log.error("Failed to sync author with Search Service: {}", e.getMessage(), e);
        }

        return savedAuthor;
    }




    public List<Author> getAuthorsByPaper(Integer paperId) {
        ResearchPaper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Paper not found"));

        return authorRepository.findByPaper(paper);
    }


    public Author removeAuthor(Integer paperId, Integer authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        if (!author.getPaper().getId().equals(paperId)) {
            throw new RuntimeException("Author does not belong to this paper");
        }

        authorRepository.delete(author);

        // Call search-service for syncing deletion
        try {
            String url = searchServiceUrl + "/authors/sync/" + authorId; // put ID in URL
            log.debug("Calling Search Service DELETE at: {}", url);

            // Extract the user's Bearer token
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) {
                log.warn("No request attributes available; skipping token propagation");
            } else {
                String bearerToken = attrs.getRequest().getHeader("Authorization");
                if (bearerToken != null && !bearerToken.isEmpty()) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Authorization", bearerToken);
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    // DELETE should not have a body
                    HttpEntity<Void> entity = new HttpEntity<>(headers);

                    ResponseEntity<Void> response = restTemplate.exchange(
                            url,
                            HttpMethod.DELETE,
                            entity,
                            Void.class
                    );

                    log.debug("Sync DELETE response status: {}", response.getStatusCode());
                } else {
                    log.warn("No Authorization header found in the incoming request");
                }
            }
        } catch (Exception e) {
            log.error("Failed to delete author in Search Service: {}", e.getMessage(), e);
        }


        return author;
    }


}
