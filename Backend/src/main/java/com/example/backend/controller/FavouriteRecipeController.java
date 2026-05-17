package com.example.backend.controller;

import com.example.backend.dto.RecipeResponseDto;
import com.example.backend.security.AuthenticatedUser;
import com.example.backend.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class FavouriteRecipeController {
    private final RecipeService recipeService;

    @GetMapping("/favourites")
    public ResponseEntity<List<RecipeResponseDto>> getFavourites(
            @AuthenticationPrincipal AuthenticatedUser principal
    ) {
        return ResponseEntity.ok(recipeService.getFavouriteRecipes(principal.id()));
    }

    @GetMapping("/{id}/favourite")
    public ResponseEntity<Boolean> isRecipeFavourite(
            @PathVariable Integer id,
            @AuthenticationPrincipal AuthenticatedUser principal) {
        boolean isFavourite = recipeService.isRecipeFavourite(id, principal.id());
        return ResponseEntity.ok(isFavourite);
    }

    @PostMapping("/{id}/favourite")
    public ResponseEntity<Void> addToFavourites(
            @PathVariable Integer id,
            @AuthenticationPrincipal AuthenticatedUser principal) {
        recipeService.addToFavourites(id, principal.id());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/favourite")
    public ResponseEntity<Void> removeFromFavourites(
            @PathVariable Integer id,
            @AuthenticationPrincipal AuthenticatedUser principal) {
        recipeService.removeFromFavourites(id, principal.id());
        return ResponseEntity.ok().build();
    }
}
