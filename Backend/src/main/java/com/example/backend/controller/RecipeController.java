package com.example.backend.controller;

import com.example.backend.dto.RecipeDetailResponseDto;
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
public class RecipeController {

    private final RecipeService recipeService;


    @GetMapping("/public")
    public ResponseEntity<List<RecipeResponseDto>> getAllRecipesPublic() {
        return ResponseEntity.ok(recipeService.getAllRecipesPublic());
    }

    @GetMapping("/public/search")
    public ResponseEntity<List<RecipeResponseDto>> searchRecipesPublic(@RequestParam String query) {
        return ResponseEntity.ok(recipeService.searchRecipesPublic(query));
    }

    @GetMapping("/public/category/{categoryId}")
    public ResponseEntity<List<RecipeResponseDto>> getRecipesByCategoryPublic(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(recipeService.getRecipesByCategoryPublic(categoryId));
    }

    @PostMapping("/public/by-grocery-items")
    public ResponseEntity<List<RecipeResponseDto>> getRecipesByGroceryItemsPublic(@RequestBody List<Integer> groceryItemIds) {
        return ResponseEntity.ok(recipeService.getRecipesByGroceryItemsPublic(groceryItemIds));
    }

    @PostMapping("/public/by-exact-grocery-items")
    public ResponseEntity<List<RecipeResponseDto>> getRecipesByExactGroceryItemsPublic(@RequestBody List<Integer> groceryItemIds) {
        return ResponseEntity.ok(recipeService.getRecipesByExactGroceryItemsPublic(groceryItemIds));
    }


    @GetMapping
    public ResponseEntity<List<RecipeResponseDto>> getAllRecipesForUser(@AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(recipeService.getAllRecipesForUser(principal.id()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDetailResponseDto> getRecipeByIdForUser(
            @PathVariable Integer id,
            @AuthenticationPrincipal AuthenticatedUser principal) {
        RecipeDetailResponseDto recipe = recipeService.getRecipeByIdForUser(id, principal.id());
        if (recipe == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recipe);
    }

    @GetMapping("/search")
    public ResponseEntity<List<RecipeResponseDto>> searchRecipesForUser(
            @RequestParam String query,
            @AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(recipeService.searchRecipesForUser(query, principal.id()));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<RecipeResponseDto>> getRecipesByCategoryForUser(
            @PathVariable Integer categoryId,
            @AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(recipeService.getRecipesByCategoryForUser(categoryId, principal.id()));
    }

    @PostMapping("/by-grocery-items")
    public ResponseEntity<List<RecipeResponseDto>> getRecipesByGroceryItemsForUser(
            @RequestBody List<Integer> groceryItemIds,
            @AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(recipeService.getRecipesByGroceryItemsForUser(groceryItemIds, principal.id()));
    }

    @PostMapping("/by-exact-grocery-items")
    public ResponseEntity<List<RecipeResponseDto>> getRecipesByExactGroceryItemsForUser(
            @RequestBody List<Integer> groceryItemIds,
            @AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(recipeService.getRecipesByExactGroceryItemsForUser(groceryItemIds, principal.id()));
    }
}
