package com.ynm.usermanagementservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    // Comma-separated roles like "ROLE_USER,ROLE_ADMIN"
    @Column(nullable = false)
    private String roles;

    public Set<String> roleSet() {
        if (roles == null || roles.isBlank()) return new HashSet<>();
        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    public void setRoleSet(Set<String> roles) {
        this.roles = String.join(",", roles);
    }
}
