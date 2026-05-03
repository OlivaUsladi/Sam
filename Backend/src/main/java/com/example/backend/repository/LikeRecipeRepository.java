package com.example.backend.repository;

import com.example.backend.entity.LikeRecipeEntity;
import com.example.backend.entity.RecipeEntity;
import com.example.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRecipeRepository extends JpaRepository<LikeRecipeEntity, Long> {

    Optional<LikeRecipeEntity> findByUserAndRecipe(UserEntity user, RecipeEntity recipe);

    boolean existsByUserAndRecipe(UserEntity user, RecipeEntity recipe);

    long countByRecipe(RecipeEntity recipe);

    void deleteByUserAndRecipe(UserEntity user, RecipeEntity recipe);
}
