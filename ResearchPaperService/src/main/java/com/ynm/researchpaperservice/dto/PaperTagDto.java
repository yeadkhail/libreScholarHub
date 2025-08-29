package com.ynm.researchpaperservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaperTagDto {
    private Integer paperId;
    private Integer tagId;
}
