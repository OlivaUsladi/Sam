package com.example.backend.repository;

import com.example.backend.entity.RecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<RecipeEntity, Integer> {

    List<RecipeEntity> findByTitleContainingIgnoreCase(String title);

    List<RecipeEntity> findByCategoriesId(Integer categoryId);

    //Запрос - выводятся рецепты, где есть хотя бы 1 из заявленных ингредиентов
    @Query("SELECT DISTINCT r FROM RecipeEntity r JOIN r.recipeGroceries rg WHERE rg.groceryItem.id IN :groceryItemIds")
    List<RecipeEntity> findByGroceryItemIds(@Param("groceryItemIds") List<Integer> groceryItemIds);

    @Query("SELECT r FROM RecipeEntity r WHERE r.id IN :recipeIds")
    List<RecipeEntity> findAllByIdIn(@Param("recipeIds") List<Integer> recipeIds);
}