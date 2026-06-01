package com.example.backend.repository;

import com.example.backend.entity.RecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<RecipeEntity, Integer> {

    List<RecipeEntity> findAllByOrderByIdAsc();

    List<RecipeEntity> findByTitleContainingIgnoreCaseOrderByIdAsc(String title);

    List<RecipeEntity> findByCategoriesIdOrderByIdAsc(Integer categoryId);

    @Query("SELECT DISTINCT r FROM RecipeEntity r " +
           "JOIN r.recipeGroceries rg " +
           "WHERE rg.groceryItem.id IN :groceryItemIds " +
           "ORDER BY r.id ASC")
    List<RecipeEntity> findByGroceryItemIds(@Param("groceryItemIds") List<Integer> groceryItemIds);

    @Query("SELECT r FROM RecipeEntity r WHERE r.id IN :recipeIds ORDER BY r.id ASC")
    List<RecipeEntity> findAllByIdIn(@Param("recipeIds") List<Integer> recipeIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update RecipeEntity r set r.likesCount = " +
           "case when coalesce(r.likesCount, 0) + :delta < 0 then 0 " +
           "     else coalesce(r.likesCount, 0) + :delta end " +
           "where r.id = :recipeId")
    int adjustLikesCount(@Param("recipeId") Integer recipeId, @Param("delta") int delta);
}
