package com.ynm.searchservice.dto;

import com.ynm.searchservice.Model.ResearchPaper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {
    private Integer id;
    private ResearchPaper paper;
    private Long userId;  // just the ID, not full User
    private String position;
}