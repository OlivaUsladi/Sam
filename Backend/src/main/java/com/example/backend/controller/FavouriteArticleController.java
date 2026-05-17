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
@RequestMapping("/api/favourite-articles")
@RequiredArgsConstructor
public class FavouriteArticleController {

    private final ArticleService service;

    @GetMapping
    public List<ArticleResponseDto> getFavourites(@AuthenticationPrincipal AuthenticatedUser principal) {
        return service.getFavouriteArticles(principal.id());
    }

    @GetMapping("/{articleId}")
    public Boolean isFavourite(@AuthenticationPrincipal AuthenticatedUser principal,
                               @PathVariable Integer articleId) {
        return service.isFavourite(principal.id(), articleId);
    }

    @PostMapping("/{articleId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addToFavourites(@AuthenticationPrincipal AuthenticatedUser principal,
                                @PathVariable Integer articleId) {
        service.addToFavourites(principal.id(), articleId);
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> removeFromFavourites(@AuthenticationPrincipal AuthenticatedUser principal,
                                                     @PathVariable Integer articleId) {
        service.removeFromFavourites(principal.id(), articleId);
        return ResponseEntity.noContent().build();
    }
}
