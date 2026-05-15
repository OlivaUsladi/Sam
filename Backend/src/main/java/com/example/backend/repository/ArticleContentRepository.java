package com.example.backend.repository;

import com.example.backend.entity.ArticleContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleContentRepository extends JpaRepository<ArticleContentEntity, Integer> {
}
