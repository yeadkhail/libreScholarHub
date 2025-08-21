package com.ynm.searchservice.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "ResearchPaper")
public class ResearchPaper {
    @Id
    private Integer id;

    private String title;
    private String abstractText; // renamed (abstract is keyword)
    private String uploadPath;
    private String visibility;
    private Integer ownerId;
    private java.sql.Date createdAt;
    private Integer metric;
}