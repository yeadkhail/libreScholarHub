package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.Tag;
import org.springframework.http.ResponseEntity;
import com.ynm.searchservice.Repository.TagRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tags")
public class TagController {

    private final TagRepository tagRepository;

    public TagController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    // Called by Tag Service when a tag is created/updated
    @PostMapping("/sync")
    public ResponseEntity<String> syncTag(@RequestBody Tag tag) {
        tagRepository.save(tag); // <- updates local relational table
        return ResponseEntity.ok("Tag synced into search DB");
    }

    // Called by Tag Service when a tag is deleted
    @DeleteMapping("/sync/{id}")
    public ResponseEntity<String> deleteTag(@PathVariable Integer id) {
        tagRepository.deleteById(id);
        return ResponseEntity.ok("Tag deleted from search DB");
    }

    // Search service uses relational table when cache misses
    @GetMapping("/{id}")
    public ResponseEntity<Tag> getTag(@PathVariable Integer id) {
        return ResponseEntity.of(tagRepository.findById(id));
    }
}
