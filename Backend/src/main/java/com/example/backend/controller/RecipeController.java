package com.example.backend.controller;

import com.example.backend.dto.RecipeDetailResponseDto;
import com.example.backend.dto.RecipeResponseDto;
import com.example.backend.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    //@RequestParam - часть URL (?), необязательный параметр
    //@PathVariable - часть URL, обязательный параметр (просто через /)
    //@RequestBody - помогает «извлечь» данные из тела HTTP-запроса и преобразовать их в объект Java

    //Коды состояния ответа HTTP    https://developer.mozilla.org/ru/docs/Web/HTTP/Reference/Status
    //400 Bad Request
    //401 Unauthorized
    //200 OK
    //201 Created

    //Без авторизации
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



    //Для авторизованных

    @GetMapping
    public ResponseEntity<List<RecipeResponseDto>> getAllRecipesForUser(@RequestHeader("userId") Integer userId) {
        return ResponseEntity.ok(recipeService.getAllRecipesForUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDetailResponseDto> getRecipeByIdForUser(
            @PathVariable Integer id,
            @RequestHeader("userId") Integer userId) {
        RecipeDetailResponseDto recipe = recipeService.getRecipeByIdForUser(id, userId);
        if (recipe == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recipe);
    }

    @GetMapping("/search")
    public ResponseEntity<List<RecipeResponseDto>> searchRecipesForUser(
            @RequestParam String query,
            @RequestHeader("userId") Integer userId) {
        return ResponseEntity.ok(recipeService.searchRecipesForUser(query, userId));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<RecipeResponseDto>> getRecipesByCategoryForUser(
            @PathVariable Integer categoryId,
            @RequestHeader("userId") Integer userId) {
        return ResponseEntity.ok(recipeService.getRecipesByCategoryForUser(categoryId, userId));
    }

    @PostMapping("/by-grocery-items")
    public ResponseEntity<List<RecipeResponseDto>> getRecipesByGroceryItemsForUser(
            @RequestBody List<Integer> groceryItemIds,
            @RequestHeader("userId") Integer userId) {
        return ResponseEntity.ok(recipeService.getRecipesByGroceryItemsForUser(groceryItemIds, userId));
    }

    @PostMapping("/by-exact-grocery-items")
    public ResponseEntity<List<RecipeResponseDto>> getRecipesByExactGroceryItemsForUser(
            @RequestBody List<Integer> groceryItemIds,
            @RequestHeader("userId") Integer userId) {
        return ResponseEntity.ok(recipeService.getRecipesByExactGroceryItemsForUser(groceryItemIds, userId));
    }


}