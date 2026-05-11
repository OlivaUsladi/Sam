package com.example.backend.controller;

import com.example.backend.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class LikeRecipeController {
    private final RecipeService recipeService;

    @GetMapping("/{id}/like")
    public ResponseEntity<Boolean> isRecipeLiked(
            @PathVariable Integer id,
            @RequestHeader(value = "userId") Integer userId) {

        boolean isLiked = recipeService.isRecipeLiked(id, userId);
        return ResponseEntity.ok(isLiked);
    }

    @GetMapping("/{id}/likes_count")
    public ResponseEntity<Integer> getLikesCount(
            @PathVariable Integer id) {

        int likesCount = recipeService.getLikesCount(id);
        return ResponseEntity.ok(likesCount);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> addLike(
            @PathVariable Integer id,
            @RequestHeader(value = "userId") Integer userId) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        recipeService.addLike(id, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> removeLike(
            @PathVariable Integer id,
            @RequestHeader(value = "userId") Integer userId) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        recipeService.removeLike(id, userId);
        return ResponseEntity.ok().build();
    }
}
