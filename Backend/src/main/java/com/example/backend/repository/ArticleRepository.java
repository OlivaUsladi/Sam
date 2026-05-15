package com.example.backend.repository;

import com.example.backend.entity.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<ArticleEntity, Integer> {

    List<ArticleEntity> findAllByOrderByCreatedAtDesc();

    List<ArticleEntity> findByCategoryIdOrderByCreatedAtDesc(Integer categoryId);

    @Query(
            value = """
                    SELECT * FROM articles
                    WHERE LOWER(title) LIKE LOWER('%' || :query || '%')
                       OR LOWER(COALESCE(CAST(main_words AS TEXT), '')) LIKE LOWER('%' || :query || '%')
                    ORDER BY created_at DESC
                    """,
            nativeQuery = true
    )
    List<ArticleEntity> searchByQuery(@Param("query") String query);
}
