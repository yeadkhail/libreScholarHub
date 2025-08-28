package com.ynm.researchpaperservice.Repository;

import com.ynm.researchpaperservice.Model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    Optional<Tag> findByName(String name);
    Optional<Tag> findByNameIgnoreCase(String name);
}
