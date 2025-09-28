package com.ynm.searchservice.service;

import com.ynm.searchservice.Model.Tag;
import com.ynm.searchservice.Repository.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    //@CachePut(value = "tags", key = "#tag.id")
    public Tag syncCreateTag(Tag tag) {
        return tagRepository.save(tag);
    }

    //@CachePut(value = "tags", key = "#id")
    public Tag syncUpdateTag(Integer id, Tag tag) {
        return tagRepository.findById(id).map(existing -> {
            existing.setName(tag.getName());
            return tagRepository.save(existing);
        }).orElseGet(() -> tagRepository.save(tag));
    }

    //@CacheEvict(value = "tags", key = "#id")
    public void syncDeleteTag(Integer id) {
        if (tagRepository.existsById(id)) {
            tagRepository.deleteById(id);
        }
    }

    //@Cacheable(value = "tags", key = "#id")
    public Tag getTag(Integer id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
    }

    @Cacheable(value = "allTags")
    public List<Tag> getAllTags() {
        return (List<Tag>) tagRepository.findAll();
    }
}
