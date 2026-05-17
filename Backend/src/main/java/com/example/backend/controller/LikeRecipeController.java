package com.example.backend.controller;

import com.example.backend.security.AuthenticatedUser;
import com.example.backend.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class LikeRecipeController {
    private final RecipeService recipeService;

    @GetMapping("/{id}/like")
    public ResponseEntity<Boolean> isRecipeLiked(
            @PathVariable Integer id,
            @AuthenticationPrincipal AuthenticatedUser principal) {

        boolean isLiked = recipeService.isRecipeLiked(id, principal.id());
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
            @AuthenticationPrincipal AuthenticatedUser principal) {
        recipeService.addLike(id, principal.id());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> removeLike(
            @PathVariable Integer id,
            @AuthenticationPrincipal AuthenticatedUser principal) {
        recipeService.removeLike(id, principal.id());
        return ResponseEntity.ok().build();
    }
}
