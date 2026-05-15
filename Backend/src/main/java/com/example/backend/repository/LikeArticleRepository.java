package com.example.backend.repository;

import com.example.backend.entity.LikeArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeArticleRepository extends JpaRepository<LikeArticleEntity, Long> {

    List<LikeArticleEntity> findByUserId(Integer userId);

    boolean existsByUserIdAndArticleId(Integer userId, Integer articleId);

    long countByArticleId(Integer articleId);

    void deleteByUserIdAndArticleId(Integer userId, Integer articleId);
}
