package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.Author;
import com.ynm.searchservice.dto.AuthorDto;
import com.ynm.searchservice.service.AuthorService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
@AllArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping("/sync")
    public ResponseEntity<String> syncAuthor(@RequestBody AuthorDto dto) {
        authorService.syncAuthor(dto);
        return ResponseEntity.ok("Author synced");
    }

    @DeleteMapping("/sync/{authorId}")
    public ResponseEntity<String> deleteAuthor(@PathVariable Integer authorId) {
        authorService.deleteAuthor(authorId);
        return ResponseEntity.ok("Author removed");
    }

    @GetMapping("/paper/{paperId}")
    public ResponseEntity<List<Author>> getAuthorsByPaper(@PathVariable Integer paperId) {
        return ResponseEntity.ok(authorService.getAuthorsByPaper(paperId));
    }
}
