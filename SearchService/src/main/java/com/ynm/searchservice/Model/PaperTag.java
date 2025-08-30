package com.ynm.searchservice.Model;

import com.ynm.searchservice.Model.ResearchPaper;
import com.ynm.searchservice.Model.Tag;
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
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "paper_id")
    private ResearchPaper paper;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;
}
