package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.Tag;
import com.ynm.searchservice.Repository.TagRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/tags")
public class TagController {

    private final TagRepository tagRepository;

    public TagController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    // Sync: Create a new tag
    @PostMapping("/sync")
    public ResponseEntity<String> syncCreateTag(@RequestBody Tag tag) {
        tagRepository.save(tag);
        return ResponseEntity.ok("Tag created/synced in search DB");
    }

    // Sync: Update an existing tag
    @PutMapping("/sync/{id}")
    public ResponseEntity<String> syncUpdateTag(@PathVariable Integer id, @RequestBody Tag tag) {
        Optional<Tag> existing = tagRepository.findById(id);
        if (existing.isPresent()) {
            Tag t = existing.get();
            t.setName(tag.getName());
            tagRepository.save(t);
            return ResponseEntity.ok("Tag updated in search DB");
        } else {
            // If not exist, create new (optional)
            tagRepository.save(tag);
            return ResponseEntity.ok("Tag did not exist, created new in search DB");
        }
    }

    // Sync: Delete a tag
    @DeleteMapping("/sync/{id}")
    public ResponseEntity<String> syncDeleteTag(@PathVariable Integer id) {
        if (tagRepository.existsById(id)) {
            tagRepository.deleteById(id);
            return ResponseEntity.ok("Tag deleted from search DB");
        } else {
            return ResponseEntity.ok("Tag not found; nothing to delete");
        }
    }

    // Retrieve tag (cache miss or lookup)
    @GetMapping("/{id}")
    public ResponseEntity<Tag> getTag(@PathVariable Integer id) {
        return ResponseEntity.of(tagRepository.findById(id));
    }

    // Retrieve all tags (optional)
    @GetMapping
    public ResponseEntity<Iterable<Tag>> getAllTags() {
        return ResponseEntity.ok(tagRepository.findAll());
    }
}
