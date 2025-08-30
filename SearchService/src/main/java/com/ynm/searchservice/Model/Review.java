package com.ynm.searchservice.Model;

import com.ynm.searchservice.Model.ResearchPaper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Review")
public class Review {
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "paper_id", nullable = false)
    private ResearchPaper paper;

    private Integer score;
    private String comment;
    private Date timestamp;
}
