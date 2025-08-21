package com.ynm.searchservice.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PaperTag")
public class PaperTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "paper_id")
    private ResearchPaper paper;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;
}
