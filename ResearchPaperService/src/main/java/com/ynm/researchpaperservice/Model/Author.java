package com.ynm.researchpaperservice.Model;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Author")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // recommended to add ID

    @ManyToOne
    @JoinColumn(name = "paper_id", nullable = false)
    private ResearchPaper paper;

    private Long userId;
    private String position;
}

