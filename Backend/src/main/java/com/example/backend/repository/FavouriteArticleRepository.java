package com.example.backend.repository;

import com.example.backend.entity.FavouriteArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavouriteArticleRepository extends JpaRepository<FavouriteArticleEntity, Long> {

    List<FavouriteArticleEntity> findByUserId(Integer userId);

    boolean existsByUserIdAndArticleId(Integer userId, Integer articleId);

    void deleteByUserIdAndArticleId(Integer userId, Integer articleId);
}
