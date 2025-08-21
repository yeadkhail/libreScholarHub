package com.ynm.searchservice.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Citation")
public class Citation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cited_paper_id")
    private ResearchPaper citedPaper;

    @ManyToOne
    @JoinColumn(name = "citing_paper_id")
    private ResearchPaper citingPaper;
}