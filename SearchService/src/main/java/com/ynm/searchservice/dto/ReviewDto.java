package com.ynm.searchservice.dto;

import com.ynm.searchservice.Controller.PaperTagController;
import com.ynm.searchservice.Model.ResearchPaper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Integer id;
    private Long userId;
    private ResearchPaper paper;
    private Integer score;
    private String comment;
    private Date timestamp;
}
