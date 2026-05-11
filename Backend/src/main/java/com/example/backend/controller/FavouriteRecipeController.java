package com.example.backend.controller;

import com.example.backend.dto.RecipeResponseDto;
import com.example.backend.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class FavouriteRecipeController {
    private final RecipeService recipeService;

    @GetMapping("/favourites")
    public ResponseEntity<List<RecipeResponseDto>> getFavourites(
            @RequestHeader("userId") Integer userId
    ) {
        return ResponseEntity.ok(recipeService.getFavouriteRecipes(userId));
    }

    @GetMapping("/{id}/favourite")
    public ResponseEntity<Boolean> isRecipeFavourite(
            @PathVariable Integer id,
            @RequestHeader("userId") Integer userId) {
        boolean isFavourite = recipeService.isRecipeFavourite(id, userId);
        return ResponseEntity.ok(isFavourite);
    }

    @PostMapping("/{id}/favourite")
    public ResponseEntity<Void> addToFavourites(
            @PathVariable Integer id,
            @RequestHeader("userId") Integer userId) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        recipeService.addToFavourites(id, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/favourite")
    public ResponseEntity<Void> removeFromFavourites(
            @PathVariable Integer id,
            @RequestHeader("userId") Integer userId) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        recipeService.removeFromFavourites(id, userId);
        return ResponseEntity.ok().build();
    }
}
