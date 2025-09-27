package com.ynm.searchservice.service;

import com.ynm.searchservice.Model.Author;
import com.ynm.searchservice.Model.ResearchPaper;
import com.ynm.searchservice.Model.User;
import com.ynm.searchservice.Repository.AuthorRepository;
import com.ynm.searchservice.Repository.UserRepository;
import com.ynm.searchservice.dto.AuthorDto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final UserRepository userRepository;

    // Save or update Author and cache it
    @CachePut(value = "authors", key = "#dto.id")
    public Author syncAuthor(AuthorDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ResearchPaper paper = dto.getPaper();

        Author author = new Author();
        author.setId(dto.getId());
        author.setUser(user);
        author.setPaper(paper);
        author.setPosition(dto.getPosition());

        return authorRepository.save(author);
    }

    // Delete Author and evict from cache
    @CacheEvict(value = "authors", key = "#authorId")
    public void deleteAuthor(Integer authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        authorRepository.delete(author);
    }

    // Get Authors by Paper (cache result by paperId)
    @Cacheable(value = "authorsByPaper", key = "#paperId")
    public List<Author> getAuthorsByPaper(Integer paperId) {
        return authorRepository.findByPaperId(paperId);
    }
}
