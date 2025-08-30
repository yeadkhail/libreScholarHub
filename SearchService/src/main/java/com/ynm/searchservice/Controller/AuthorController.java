package com.ynm.searchservice.Controller;

import com.ynm.searchservice.Model.Author;
import com.ynm.searchservice.Model.ResearchPaper;
import com.ynm.searchservice.Model.User;
import com.ynm.searchservice.Repository.AuthorRepository;
import com.ynm.searchservice.Repository.ResearchPaperRepository;
import com.ynm.searchservice.Repository.UserRepository;
import com.ynm.searchservice.dto.AuthorDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@RestController
@RequestMapping("/authors")
@AllArgsConstructor
public class AuthorController {

    private final AuthorRepository authorRepository;
    private final ResearchPaperRepository researchPaperRepository;
    private final UserRepository userRepository;

    @PostMapping("/sync")
    public ResponseEntity<String> syncAuthor(@RequestBody AuthorDto dto) {
        // Fetch the managed User entity
        System.out.println(dto);
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch the managed ResearchPaper entity
        ResearchPaper paper = dto.getPaper();

        // Create Author entity
        Author author = new Author();
        author.setId(dto.getId());
        author.setUser(user);
        author.setPaper(paper);
        author.setPosition(dto.getPosition());

        authorRepository.save(author);
        return ResponseEntity.ok("Author synced");
    }


    @DeleteMapping("/sync/{authorId}")
    public ResponseEntity<String> deleteAuthor(@PathVariable Integer authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        authorRepository.delete(author);

        return ResponseEntity.ok("Author removed");
    }


    @GetMapping("/paper/{paperId}")
    public ResponseEntity<List<Author>> getAuthorsByPaper(@PathVariable Integer paperId) {
        return ResponseEntity.ok(authorRepository.findByPaperId(paperId));
    }
}
