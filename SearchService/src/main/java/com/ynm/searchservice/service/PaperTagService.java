package com.ynm.searchservice.service;

import com.ynm.searchservice.Model.PaperTag;
import com.ynm.searchservice.Repository.PaperTagRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PaperTagService {

    private final PaperTagRepository paperTagRepository;

    //@CachePut(value = "paperTags", key = "#paperTag.id")
    public PaperTag syncPaperTag(PaperTag paperTag) {
        return paperTagRepository.save(paperTag);
    }

    //@CacheEvict(value = "paperTags", key = "#id")
    public void deletePaperTag(Integer id) {
        if (!paperTagRepository.existsById(id)) {
            throw new RuntimeException("PaperTag not found");
        }
        paperTagRepository.deleteById(id);
    }

    //@Cacheable(value = "tagsByPaper", key = "#paperId")
    public List<PaperTag> getTagsByPaper(int paperId) {
        return paperTagRepository.findByPaperId(paperId);
    }
}
