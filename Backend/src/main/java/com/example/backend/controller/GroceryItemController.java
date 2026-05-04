package com.example.backend.controller;

import com.example.backend.dto.GroceryItemResponseDto;
import com.example.backend.entity.GroceryItemEntity;
import com.example.backend.repository.GroceryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/grocery-items")
@RequiredArgsConstructor
public class GroceryItemController {

    private final GroceryItemRepository groceryItemRepository;

    @GetMapping
    public ResponseEntity<List<GroceryItemResponseDto>> getAllGroceryItems() {
        List<GroceryItemResponseDto> items = groceryItemRepository.findAll().stream()
                .map(item -> GroceryItemResponseDto.builder()
                        .id(item.getId())
                        .groceryId(item.getGrocery().getId())
                        .name(item.getName())
                        .defaultUnit(item.getUnit())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(items);
    }
}