package com.example.backend.repository;

import com.example.backend.entity.ShoppingListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingListEntity, Integer> {

    List<ShoppingListEntity> findByUserIdOrderByCreatedAtDesc(Integer userId);

    Optional<ShoppingListEntity> findByIdAndUserId(Integer id, Integer userId);
}

