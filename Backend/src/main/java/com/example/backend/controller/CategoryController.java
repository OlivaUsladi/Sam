package com.example.backend.controller;

import com.example.backend.dto.CategoryResponseDto;
import com.example.backend.entity.CategoryEntity;
import com.example.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        List<CategoryResponseDto> categories = categoryRepository.findAll().stream()
                .map(cat -> CategoryResponseDto.builder()
                        .id(cat.getId())
                        .name(cat.getName())
                        .description(cat.getDescription())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }
}