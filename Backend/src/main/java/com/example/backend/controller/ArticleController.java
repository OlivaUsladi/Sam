package com.example.backend.controller;

import com.example.backend.dto.ArticleDetailResponseDto;
import com.example.backend.dto.ArticleResponseDto;
import com.example.backend.security.AuthenticatedUser;
import com.example.backend.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService service;

    @GetMapping
    public List<ArticleResponseDto> getArticles(@AuthenticationPrincipal AuthenticatedUser principal) {
        return service.getArticles(principal.id());
    }

    @GetMapping("/{articleId}")
    public ArticleDetailResponseDto getArticle(@AuthenticationPrincipal AuthenticatedUser principal,
                                               @PathVariable Integer articleId) {
        return service.getArticle(principal.id(), articleId);
    }

    @GetMapping("/search")
    public List<ArticleResponseDto> searchArticles(@AuthenticationPrincipal AuthenticatedUser principal,
                                                   @RequestParam("query") String query) {
        return service.searchArticles(principal.id(), query);
    }

    @GetMapping("/category/{categoryId}")
    public List<ArticleResponseDto> getArticlesByCategory(@AuthenticationPrincipal AuthenticatedUser principal,
                                                          @PathVariable Integer categoryId) {
        return service.getArticlesByCategory(principal.id(), categoryId);
    }
}
