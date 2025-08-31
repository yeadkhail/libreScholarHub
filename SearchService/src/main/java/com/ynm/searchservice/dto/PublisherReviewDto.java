package com.ynm.searchservice.dto;

import com.ynm.searchservice.Model.ResearchPaper;
import lombok.Data;
import java.io.Serializable;

@Data
public class PublisherReviewDto implements Serializable {
    private Integer id;
    private Integer uniPubId;
    private ResearchPaper paper;
    private String reviewText;
    private Float reviewScore;
}
