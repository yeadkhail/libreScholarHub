package com.ynm.searchservice.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Author")
@ToString(exclude = {"user", "paper"})
public class Author {
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "paper_id", nullable = false)
    private ResearchPaper paper;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private String position;
}

