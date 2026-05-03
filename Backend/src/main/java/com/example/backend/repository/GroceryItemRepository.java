package com.example.backend.repository;

import com.example.backend.entity.GroceryItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GroceryItemRepository extends JpaRepository<GroceryItemEntity, Integer> {
    List<GroceryItemEntity> findByGroceryId(Integer groceryId);
}