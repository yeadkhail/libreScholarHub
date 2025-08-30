package com.ynm.searchservice.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    private Integer id;

    private String name;
    private String email;
    private String affiliation;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Author> authors;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Review> reviews;
}
