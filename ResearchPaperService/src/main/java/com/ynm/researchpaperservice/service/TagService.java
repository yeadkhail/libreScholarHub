package com.ynm.researchpaperservice.Service;
import com.ynm.researchpaperservice.Model.Tag;
import com.ynm.researchpaperservice.Repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;


    public Tag createTag(Tag tag) {
        tagRepository.findByNameIgnoreCase(tag.getName())
                .ifPresent(existing -> {
                    throw new RuntimeException("Tag already exists with name: " + tag.getName());
                });
        return tagRepository.save(tag);
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
        return tagRepository.save(existingTag);
    }

    public Tag deleteTag(Integer id) {
        if (!tagRepository.existsById(id)) {
            throw new RuntimeException("Tag not found with id: " + id);
        }
        Tag tagToDelete = getTagById(id); // fetch before deletion
        tagRepository.deleteById(id);
        return tagToDelete;
    }
}

