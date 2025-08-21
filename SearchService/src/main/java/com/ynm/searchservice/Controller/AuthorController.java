package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.Author;
import com.ynm.searchservice.Repository.AuthorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorRepository authorRepository;

    public AuthorController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncAuthor(@RequestBody Author author) {
        authorRepository.save(author);
        return ResponseEntity.ok("Author synced");
    }

    @DeleteMapping("/sync")
    public ResponseEntity<String> deleteAuthor(@RequestBody Author author) {
        authorRepository.delete(author);
        return ResponseEntity.ok("Author removed");
    }

    @GetMapping("/paper/{paperId}")
    public ResponseEntity<List<Author>> getAuthorsByPaper(@PathVariable Integer paperId) {
        return ResponseEntity.ok(authorRepository.findByPaperId(paperId));
    }
}
