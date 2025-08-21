package com.ynm.searchservice.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// This entity uses a composite primary key defined in AuthorId.java
@Getter
@Setter
@Entity
@Table(name = "Author")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // recommended to add ID

    @ManyToOne
    @JoinColumn(name = "paper_id", nullable = false)
    private ResearchPaper paper;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;
    private String position;
}