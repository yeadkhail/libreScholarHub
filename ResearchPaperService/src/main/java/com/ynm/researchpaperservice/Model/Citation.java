package com.ynm.researchpaperservice.Model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    private float lastUpdate;
}
