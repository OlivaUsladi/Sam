package com.example.backend.repository;

import com.example.backend.entity.FavouriteRecipeEntity;
import com.example.backend.entity.RecipeEntity;
import com.example.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavouriteRecipeRepository extends JpaRepository<FavouriteRecipeEntity, Long> {

    Optional<FavouriteRecipeEntity> findByUserAndRecipe(UserEntity user, RecipeEntity recipe);

    boolean existsByUserAndRecipe(UserEntity user, RecipeEntity recipe);

    List<FavouriteRecipeEntity> findByUser(UserEntity user);

    void deleteByUserAndRecipe(UserEntity user, RecipeEntity recipe);
}