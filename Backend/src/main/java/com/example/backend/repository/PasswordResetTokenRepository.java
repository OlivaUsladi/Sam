package com.example.backend.repository;

import com.example.backend.entity.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Long> {

    Optional<PasswordResetTokenEntity> findByTokenHash(String tokenHash);

    //Пометить все непогашенные токены пользователя как used (после успешного reset).
    @Modifying
    @Query("UPDATE PasswordResetTokenEntity p SET p.used = true WHERE p.userId = :userId AND p.used = false")
    int invalidateAllForUser(@Param("userId") Integer userId);
}

