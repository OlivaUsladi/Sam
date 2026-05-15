package com.example.backend.controller;

import com.example.backend.dto.ArticleDetailResponseDto;
import com.example.backend.dto.ArticleResponseDto;
import com.example.backend.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService service;

    @GetMapping
    public List<ArticleResponseDto> getArticles(@RequestHeader("userId") Integer userId) {
        return service.getArticles(userId);
    }

    @GetMapping("/{articleId}")
    public ArticleDetailResponseDto getArticle(@RequestHeader("userId") Integer userId,
                                               @PathVariable Integer articleId) {
        return service.getArticle(userId, articleId);
    }

    @GetMapping("/search")
    public List<ArticleResponseDto> searchArticles(@RequestHeader("userId") Integer userId,
                                                   @RequestParam("query") String query) {
        return service.searchArticles(userId, query);
    }

    @GetMapping("/category/{categoryId}")
    public List<ArticleResponseDto> getArticlesByCategory(@RequestHeader("userId") Integer userId,
                                                          @PathVariable Integer categoryId) {
        return service.getArticlesByCategory(userId, categoryId);
    }
}
