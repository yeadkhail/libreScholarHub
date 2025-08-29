package com.ynm.researchpaperservice.service;

import com.ynm.researchpaperservice.Model.Author;
import com.ynm.researchpaperservice.Model.ResearchPaper;
import com.ynm.researchpaperservice.Repository.AuthorRepository;
import com.ynm.researchpaperservice.Repository.ResearchPaperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final ResearchPaperRepository paperRepository;

    /**
     * Add an author to a paper
     */
    public Author addAuthor(Integer paperId, Author author) {
        ResearchPaper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Paper not found"));

        author.setPaper(paper);
        return authorRepository.save(author);
    }

    /**
     * Get all authors for a paper
     */
    public List<Author> getAuthorsByPaper(Integer paperId) {
        ResearchPaper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Paper not found"));

        return authorRepository.findByPaper(paper);
    }

    /**
     * Remove an author from a paper
     */
    public Author  removeAuthor(Integer paperId, Integer authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        if (!author.getPaper().getId().equals(paperId)) {
            throw new RuntimeException("Author does not belong to this paper");
        }

        authorRepository.delete(author);
        return author;
    }
}
