package com.example.backend.repository;

import com.example.backend.entity.GroceryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroceryRepository extends JpaRepository<GroceryEntity, Integer> {
}