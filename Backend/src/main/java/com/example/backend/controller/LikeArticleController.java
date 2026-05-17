package com.example.backend.controller;

import com.example.backend.dto.ArticleResponseDto;
import com.example.backend.security.AuthenticatedUser;
import com.example.backend.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes-articles")
@RequiredArgsConstructor
public class LikeArticleController {

    private final ArticleService service;

    @GetMapping
    public List<ArticleResponseDto> getLiked(@AuthenticationPrincipal AuthenticatedUser principal) {
        return service.getLikedArticles(principal.id());
    }

    @GetMapping("/{articleId}")
    public Boolean isLiked(@AuthenticationPrincipal AuthenticatedUser principal,
                           @PathVariable Integer articleId) {
        return service.isLiked(principal.id(), articleId);
    }

    @PostMapping("/{articleId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addLike(@AuthenticationPrincipal AuthenticatedUser principal,
                        @PathVariable Integer articleId) {
        service.addLike(principal.id(), articleId);
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> removeLike(@AuthenticationPrincipal AuthenticatedUser principal,
                                           @PathVariable Integer articleId) {
        service.removeLike(principal.id(), articleId);
        return ResponseEntity.noContent().build();
    }
}
