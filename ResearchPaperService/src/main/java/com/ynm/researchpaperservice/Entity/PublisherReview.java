package com.ynm.researchpaperservice.Entity;
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

    private Integer uni_pub_id;

    @ManyToOne
    @JoinColumn(name = "paper_id")
    private ResearchPaper paper;
}

