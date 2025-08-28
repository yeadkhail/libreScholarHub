package com.ynm.researchpaperservice.dto;

import lombok.Data;

@Data
public class CitationDto {
    private Integer citingPaperId;
    private Integer citedPaperId;
}
