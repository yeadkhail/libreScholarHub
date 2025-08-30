package com.ynm.searchservice.Model;

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
    private Integer id;

    private String title;
    private String abstractText;
    private String uploadPath;
    private String visibility;
    private Integer ownerId;
    private java.sql.Date createdAt;
    private Integer metric;
}
