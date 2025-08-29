package com.ynm.researchpaperservice.dto;

import lombok.Data;

@Data
public class PublisherReviewDto {
    private Integer uniPubId;
    private String reviewText;
    private Float reviewScore;
}
