package com.example.backend.controller;

import com.example.backend.dto.ArticleResponseDto;
import com.example.backend.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favourite-articles")
@RequiredArgsConstructor
public class FavouriteArticleController {

    private final ArticleService service;

    @GetMapping
    public List<ArticleResponseDto> getFavourites(@RequestHeader("userId") Integer userId) {
        return service.getFavouriteArticles(userId);
    }

    @GetMapping("/{articleId}")
    public Boolean isFavourite(@RequestHeader("userId") Integer userId,
                               @PathVariable Integer articleId) {
        return service.isFavourite(userId, articleId);
    }

    @PostMapping("/{articleId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addToFavourites(@RequestHeader("userId") Integer userId,
                                @PathVariable Integer articleId) {
        service.addToFavourites(userId, articleId);
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> removeFromFavourites(@RequestHeader("userId") Integer userId,
                                                     @PathVariable Integer articleId) {
        service.removeFromFavourites(userId, articleId);
        return ResponseEntity.noContent().build();
    }
}

