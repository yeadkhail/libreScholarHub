package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.Tag;
import com.ynm.searchservice.service.TagService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
@AllArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping("/sync")
    public ResponseEntity<String> syncCreateTag(@RequestBody Tag tag) {
        tagService.syncCreateTag(tag);
        return ResponseEntity.ok("Tag created/synced in search DB");
    }

    @PutMapping("/sync/{id}")
    public ResponseEntity<String> syncUpdateTag(@PathVariable Integer id, @RequestBody Tag tag) {
        tagService.syncUpdateTag(id, tag);
        return ResponseEntity.ok("Tag updated or created in search DB");
    }

    @DeleteMapping("/sync/{id}")
    public ResponseEntity<String> syncDeleteTag(@PathVariable Integer id) {
        tagService.syncDeleteTag(id);
        return ResponseEntity.ok("Tag deleted if existed in search DB");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tag> getTag(@PathVariable Integer id) {
        return ResponseEntity.ok(tagService.getTag(id));
    }

    @GetMapping
    public ResponseEntity<List<Tag>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }
}
