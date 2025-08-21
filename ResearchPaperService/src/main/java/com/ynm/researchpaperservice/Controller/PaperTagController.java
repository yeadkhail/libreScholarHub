package com.ynm.researchpaperservice.Controller;

import com.ynm.researchpaperservice.Entity.PaperTag;
import com.ynm.researchpaperservice.Entity.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/papers/{paperId}/tags")
@RequiredArgsConstructor
public class PaperTagController {

//    private final PaperTagService tagService;

    @PostMapping
    public ResponseEntity<PaperTag> addTag(@PathVariable Integer paperId, @RequestBody PaperTag paperTag) {
//        return ResponseEntity.ok(tagService.addTagToPaper(paperId, paperTag));
        PaperTag dummy = new PaperTag();
        dummy.setId(1);
        dummy.setPaper(null); // no actual paper
        dummy.setTag(null);   // no actual tag
        return ResponseEntity.ok(dummy);
    }

    @GetMapping
    public ResponseEntity<List<Tag>> getTags(@PathVariable Integer paperId) {
//        return ResponseEntity.ok(tagService.getTagsByPaper(paperId));
        Tag tag1 = new Tag();
        tag1.setId(101);
        tag1.setName("Machine Learning");

        Tag tag2 = new Tag();
        tag2.setId(102);
        tag2.setName("Networking");

        List<Tag> list = new ArrayList<>();
        list.add(tag1);
        list.add(tag2);

        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<String> removeTag(@PathVariable Integer paperId, @PathVariable Integer tagId) {
//        tagService.removeTagFromPaper(paperId, tagId);
//        return ResponseEntity.noContent().build();
        return ResponseEntity.ok("Called removeTag for paperId=" + paperId + ", tagId=" + tagId);
    }
}
