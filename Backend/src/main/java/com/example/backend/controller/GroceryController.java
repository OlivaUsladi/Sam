package com.example.backend.controller;

import com.example.backend.dto.GroceryResponseDto;
import com.example.backend.entity.GroceryEntity;
import com.example.backend.repository.GroceryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groceries")
@RequiredArgsConstructor
public class GroceryController {

    private final GroceryRepository groceryRepository;

    @GetMapping
    public ResponseEntity<List<GroceryResponseDto>> getAllGroceries() {
        List<GroceryResponseDto> groceries = groceryRepository.findAll().stream()
                .map(g -> GroceryResponseDto.builder()
                        .id(g.getId())
                        .name(g.getName())
                        .description(g.getDescription())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(groceries);
    }
}