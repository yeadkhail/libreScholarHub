package com.ynm.searchservice.Repository;

import com.ynm.searchservice.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // find by email
    Optional<User> findByEmail(String email);

    // find by name (exact match)
    List<User> findByName(String name);

    // find by partial name (case-insensitive)
    List<User> findByNameContainingIgnoreCase(String keyword);
}
