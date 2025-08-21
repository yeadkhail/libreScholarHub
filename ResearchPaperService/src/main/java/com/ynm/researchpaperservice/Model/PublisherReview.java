package com.ynm.researchpaperservice.Model;
import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne
    @JoinColumn(name = "paper_id")
    private ResearchPaper paper;
}

