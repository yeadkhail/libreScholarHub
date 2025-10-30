package com.ynm.researchpaperservice.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "research_papers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResearchPaper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String abstractText;
    private String uploadPath;
    private String visibility;
    private Long ownerId;
    private Date createdAt;
    private Float metric;
}