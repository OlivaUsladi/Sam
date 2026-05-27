package com.example.backend.repository;

import com.example.backend.entity.SourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SourceRepository extends JpaRepository<SourceEntity, Integer> {

    List<SourceEntity> findByUserIdOrderByNameAsc(Integer userId);

    Optional<SourceEntity> findByIdAndUserId(Integer id, Integer userId);

    boolean existsByUserIdAndNameIgnoreCase(Integer userId, String name);
}
