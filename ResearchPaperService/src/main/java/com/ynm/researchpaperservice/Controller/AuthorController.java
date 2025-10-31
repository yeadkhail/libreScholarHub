package com.ynm.researchpaperservice.Controller;

import com.ynm.researchpaperservice.Model.Author;
import com.ynm.researchpaperservice.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/research-paper/papers/{paperId}/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    public ResponseEntity<Author> addAuthor(@PathVariable Integer paperId, @RequestBody Author author) {
        return ResponseEntity.ok(authorService.addAuthor(paperId, author));

    }

    @GetMapping
    public ResponseEntity<List<Author>> getAuthors(@PathVariable Integer paperId) {
        return ResponseEntity.ok(authorService.getAuthorsByPaper(paperId));

    }

    @DeleteMapping("/{authorId}")
    public ResponseEntity<String> removeAuthor(@PathVariable Integer paperId, @PathVariable Integer authorId) {
        authorService.removeAuthor(paperId, authorId);
        return ResponseEntity.noContent().build();
//        return ResponseEntity.ok("Called removeAuthor for paperId=" + paperId + ", authorId=" + authorId);
    }
}
