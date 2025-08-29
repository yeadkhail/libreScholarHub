package com.ynm.researchpaperservice.Service;

import com.ynm.researchpaperservice.Model.PaperTag;
import com.ynm.researchpaperservice.Repository.PaperTagRepository;
import com.ynm.researchpaperservice.Repository.ResearchPaperRepository;
import com.ynm.researchpaperservice.Repository.TagRepository;
import com.ynm.researchpaperservice.dto.PaperTagDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaperTagService {

    private final PaperTagRepository paperTagRepository;
    private final ResearchPaperRepository researchPaperRepository;
    private final TagRepository tagRepository;

    public PaperTag createPaperTag(PaperTagDto request) {
        Integer paperId = request.getPaperId();
        Integer tagId = request.getTagId();

        var paper = researchPaperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("Paper not found with id " + paperId));

        var tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found with id " + tagId));

        if (paperTagRepository.existsByPaperIdAndTagId(paperId, tagId)) {
            throw new RuntimeException("This tag is already assigned to the paper.");
        }

        PaperTag paperTag = new PaperTag();
        paperTag.setPaper(paper);
        paperTag.setTag(tag);

        return paperTagRepository.save(paperTag);
    }

    public List<PaperTag> getAllPaperTags() {
        return paperTagRepository.findAll();
    }

    public PaperTag getPaperTagById(Integer id) {
        return paperTagRepository.findById(id).orElse(null);
    }

    public List<PaperTag> getTagsByPaperId(Integer paperId) {
        return paperTagRepository.findByPaperId(paperId);
    }

    public List<PaperTag> getPapersByTagId(Integer tagId) {
        return paperTagRepository.findByTagId(tagId);
    }

    public PaperTag deletePaperTag(Integer id) {
        return paperTagRepository.findById(id)
                .map(paperTag -> {
                    paperTagRepository.delete(paperTag);
                    return paperTag;
                })
                .orElseThrow(() -> new RuntimeException("PaperTag not found with id " + id));
    }
}
