package com.ynm.researchpaperservice.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ResearchPaper")
public class ResearchPaper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String abstractText;
    private String uploadPath;
    private String visibility;
    private Long ownerId;
    private java.sql.Date createdAt;
    private Float metric;


    public void addMetric(float newMetric) {
        this.metric += newMetric;
    }
}
