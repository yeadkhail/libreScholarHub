package com.ynm.usermanagementservice.repository;

import com.ynm.usermanagementservice.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    void deleteByUserId(Long userId);

    boolean existsByUserIdAndToken(Long userId,String token);

    Optional<RefreshToken> findByUserId(Long userId);
}
