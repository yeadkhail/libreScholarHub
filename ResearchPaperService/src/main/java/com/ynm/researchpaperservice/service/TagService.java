package com.ynm.researchpaperservice.service;

import com.ynm.researchpaperservice.Model.Tag;
import com.ynm.researchpaperservice.Repository.TagRepository;
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
public class TagService {

    private final TagRepository tagRepository;
    private final RestTemplate restTemplate;
    private final String searchServiceUrl;

    public TagService(TagRepository tagRepository,
                      RestTemplate restTemplate,
                      @Value("${search.service.url}") String searchServiceUrl) {
        this.tagRepository = tagRepository;
        this.restTemplate = restTemplate;
        this.searchServiceUrl = searchServiceUrl;
    }

    public Tag createTag(Tag tag) {
        tagRepository.findByNameIgnoreCase(tag.getName())
                .ifPresent(existing -> {
                    throw new RuntimeException("Tag already exists with name: " + tag.getName());
                });

        Tag savedTag = tagRepository.save(tag);
        syncTagToSearchService(savedTag, HttpMethod.POST, null);
        return savedTag;
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public Tag getTagById(Integer id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));
    }

    public Tag getTagByName(String name) {
        return tagRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Tag not found with name: " + name));
    }

    public Tag updateTag(Integer id, Tag updatedTag) {
        Tag existingTag = getTagById(id);
        tagRepository.findByNameIgnoreCase(updatedTag.getName())
                .ifPresent(duplicate -> {
                    if (!duplicate.getId().equals(id)) {
                        throw new RuntimeException("Another tag already exists with name: " + updatedTag.getName());
                    }
                });

        existingTag.setName(updatedTag.getName());
        Tag savedTag = tagRepository.save(existingTag);
        syncTagToSearchService(savedTag, HttpMethod.PUT, id);
        return savedTag;
    }

    public Tag deleteTag(Integer id) {
        if (!tagRepository.existsById(id)) {
            throw new RuntimeException("Tag not found with id: " + id);
        }
        Tag tagToDelete = getTagById(id); // fetch before deletion
        tagRepository.deleteById(id);
        syncTagToSearchService(tagToDelete, HttpMethod.DELETE, id);
        return tagToDelete;
    }

    /** Sync tag to SearchService */
    private void syncTagToSearchService(Tag tag, HttpMethod method, Integer id) {
        try {
            String url = searchServiceUrl + "/tags/sync";
            if (id != null && method == HttpMethod.PUT || method == HttpMethod.DELETE) {
                url += "/" + id;
            }

            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            if (attrs != null) {
                String bearerToken = attrs.getRequest().getHeader("Authorization");
                if (bearerToken != null && !bearerToken.isEmpty()) {
                    headers.set("Authorization", bearerToken);
                }
            }

            HttpEntity<Tag> entity = method == HttpMethod.DELETE ? new HttpEntity<>(headers) : new HttpEntity<>(tag, headers);

            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    method,
                    entity,
                    Void.class
            );
            log.debug("Synced tag {} with SearchService, status: {}", tag.getName(), response.getStatusCode());
        } catch (Exception e) {
            log.error("Failed to sync tag {} with SearchService: {}", tag.getName(), e.getMessage(), e);
        }
    }
}
