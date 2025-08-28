package com.ynm.researchpaperservice.Controller;

import com.ynm.researchpaperservice.Model.Tag;
import com.ynm.researchpaperservice.Service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @PostMapping
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag) {
        return ResponseEntity.ok(tagService.createTag(tag));
    }

    @GetMapping
    public ResponseEntity<List<Tag>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tag> getTagById(@PathVariable Integer id) {
        try {
            Tag tag = tagService.getTagById(id);
            return ResponseEntity.ok(tag); // 200 if found
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // 404 if not found
        }
    }

    @GetMapping("/byName/{name}")
    public ResponseEntity<Tag> getTagByName(@PathVariable String name) {
        try {
            Tag tag = tagService.getTagByName(name);
            return ResponseEntity.ok(tag); // 200 if found
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // 404 if not found
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tag> updateTag(@PathVariable Integer id, @RequestBody Tag updatedTag) {
        try {
            Tag updated = tagService.updateTag(id, updatedTag);
            return ResponseEntity.ok(updated); // 200 with updated tag
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build(); // 400 if duplicate or not found
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Tag> deleteTag(@PathVariable Integer id) {
        try {
            Tag deleted = tagService.deleteTag(id);
            return ResponseEntity.ok(deleted); // 200 with deleted tag
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // 404 if not found
        }
    }

}
