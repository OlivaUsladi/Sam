package com.example.backend.repository;

import com.example.backend.entity.GoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<GoalEntity, Integer> {

    List<GoalEntity> findByUserIdOrderByCreatedAtDesc(Integer userId);

    Optional<GoalEntity> findByIdAndUserId(Integer id, Integer userId);
}
