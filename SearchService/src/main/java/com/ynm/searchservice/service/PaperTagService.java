package com.ynm.searchservice.service;

import com.ynm.searchservice.Model.PaperTag;
import com.ynm.searchservice.Repository.PaperTagRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PaperTagService {

    private final PaperTagRepository paperTagRepository;

    public PaperTag syncPaperTag(PaperTag paperTag) {
        return paperTagRepository.save(paperTag);
    }

    public void deletePaperTag(Integer id) {
        if (!paperTagRepository.existsById(id)) {
            throw new RuntimeException("PaperTag not found");
        }
        paperTagRepository.deleteById(id);
    }

    public List<PaperTag> getTagsByPaper(int paperId) {
        return paperTagRepository.findByPaperId(paperId);
    }
}
