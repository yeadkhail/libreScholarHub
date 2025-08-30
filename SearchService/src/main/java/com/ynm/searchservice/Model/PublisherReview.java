package com.ynm.searchservice.Model;

import com.ynm.searchservice.Model.ResearchPaper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "publisher_review")
public class PublisherReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer uniPubId;
    private String reviewText;
    private Float reviewScore;
    @ManyToOne
    @JoinColumn(name = "paper_id")
    private ResearchPaper paper;
}

