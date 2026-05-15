package com.example.backend.controller;

import com.example.backend.dto.ArticleCategoryResponseDto;
import com.example.backend.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/article-categories")
@RequiredArgsConstructor
public class ArticleCategoryController {

    private final ArticleService service;

    @GetMapping
    public List<ArticleCategoryResponseDto> getCategories() {
        return service.getCategories();
    }
}

