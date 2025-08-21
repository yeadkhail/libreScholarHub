package com.ynm.researchpaperservice.Controller;

import com.ynm.researchpaperservice.Model.Author;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/papers/{paperId}/authors")
@RequiredArgsConstructor
public class AuthorController {

//    private final AuthorService authorService;

    @PostMapping
    public ResponseEntity<Author> addAuthor(@PathVariable Integer paperId, @RequestBody Author author) {
//        return ResponseEntity.ok(authorService.addAuthor(paperId, author));
        Author dummy = new Author();
        dummy.setId(1);
        dummy.setPaper(null); // no actual paper
        dummy.setUserId(author.getUserId());
        dummy.setPosition(author.getPosition() != null ? author.getPosition() : "Dummy position");
        return ResponseEntity.ok(dummy);
    }

    @GetMapping
    public ResponseEntity<List<Author>> getAuthors(@PathVariable Integer paperId) {
//        return ResponseEntity.ok(authorService.getAuthorsByPaper(paperId));
        Author dummy1 = new Author();
        dummy1.setId(1);
        dummy1.setPaper(null);
        dummy1.setUserId(101);
        dummy1.setPosition("First Author");

        Author dummy2 = new Author();
        dummy2.setId(2);
        dummy2.setPaper(null);
        dummy2.setUserId(102);
        dummy2.setPosition("Co-Author");

        List<Author> list = new ArrayList<>();
        list.add(dummy1);
        list.add(dummy2);

        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{authorId}")
    public ResponseEntity<String> removeAuthor(@PathVariable Integer paperId, @PathVariable Integer authorId) {
//        authorService.removeAuthor(paperId, authorId);
//        return ResponseEntity.noContent().build();
        return ResponseEntity.ok("Called removeAuthor for paperId=" + paperId + ", authorId=" + authorId);
    }
}
