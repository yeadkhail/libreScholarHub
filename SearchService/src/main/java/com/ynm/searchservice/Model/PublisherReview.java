package com.ynm.searchservice.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "publisher_review")
public class PublisherReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer uniPubId;

    @ManyToOne
    @JoinColumn(name = "paper_id")
    private ResearchPaper paper;
}