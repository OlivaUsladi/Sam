package com.example.backend.controller;

import com.example.backend.dto.ArticleResponseDto;
import com.example.backend.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes-articles")
@RequiredArgsConstructor
public class LikeArticleController {

    private final ArticleService service;

    @GetMapping
    public List<ArticleResponseDto> getLiked(@RequestHeader("userId") Integer userId) {
        return service.getLikedArticles(userId);
    }

    @GetMapping("/{articleId}")
    public Boolean isLiked(@RequestHeader("userId") Integer userId,
                           @PathVariable Integer articleId) {
        return service.isLiked(userId, articleId);
    }

    @GetMapping("/{articleId}/count")
    public Long getLikesCount(@PathVariable Integer articleId) {
        return service.getLikesCount(articleId);
    }

    @PostMapping("/{articleId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addLike(@RequestHeader("userId") Integer userId,
                        @PathVariable Integer articleId) {
        service.addLike(userId, articleId);
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> removeLike(@RequestHeader("userId") Integer userId,
                                           @PathVariable Integer articleId) {
        service.removeLike(userId, articleId);
        return ResponseEntity.noContent().build();
    }
}