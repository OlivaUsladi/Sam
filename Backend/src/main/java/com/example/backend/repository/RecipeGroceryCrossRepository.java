package com.example.backend.repository;

import com.example.backend.entity.RecipeGroceryCrossEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecipeGroceryCrossRepository extends JpaRepository<RecipeGroceryCrossEntity, Integer> {
    List<RecipeGroceryCrossEntity> findByRecipeId(Integer recipeId);
}