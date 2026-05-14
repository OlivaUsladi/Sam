package com.example.backend.controller;

import com.example.backend.dto.AddItemsFromRecipeRequestDto;
import com.example.backend.dto.AddShoppingListItemRequestDto;
import com.example.backend.dto.CheckAllItemsRequestDto;
import com.example.backend.dto.CreateShoppingListRequestDto;
import com.example.backend.dto.MergeShoppingListsRequestDto;
import com.example.backend.dto.RenameShoppingListRequestDto;
import com.example.backend.dto.ShoppingListItemResponseDto;
import com.example.backend.dto.ShoppingListResponseDto;
import com.example.backend.dto.UpdateShoppingListItemRequestDto;
import com.example.backend.service.ShoppingListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shopping-lists")
@RequiredArgsConstructor
public class ShoppingListController {

    private final ShoppingListService service;


    @GetMapping
    public List<ShoppingListResponseDto> getLists(@RequestHeader("userId") Integer userId) {
        return service.getLists(userId);
    }

    @GetMapping("/{listId}")
    public ShoppingListResponseDto getList(@RequestHeader("userId") Integer userId,
                                           @PathVariable Integer listId) {
        return service.getList(userId, listId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingListResponseDto createList(@RequestHeader("userId") Integer userId,
                                              @Valid @RequestBody CreateShoppingListRequestDto request) {
        return service.createList(userId, request);
    }

    @PatchMapping("/{listId}")
    public ShoppingListResponseDto renameList(@RequestHeader("userId") Integer userId,
                                              @PathVariable Integer listId,
                                              @Valid @RequestBody RenameShoppingListRequestDto request) {
        return service.renameList(userId, listId, request);
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<Void> deleteList(@RequestHeader("userId") Integer userId,
                                           @PathVariable Integer listId) {
        service.deleteList(userId, listId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{listId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingListItemResponseDto addItem(@RequestHeader("userId") Integer userId,
                                               @PathVariable Integer listId,
                                               @Valid @RequestBody AddShoppingListItemRequestDto request) {
        return service.addItem(userId, listId, request);
    }

    @PatchMapping("/items/{itemId}")
    public ShoppingListItemResponseDto updateItem(@RequestHeader("userId") Integer userId,
                                                  @PathVariable Integer itemId,
                                                  @Valid @RequestBody UpdateShoppingListItemRequestDto request) {
        return service.updateItem(userId, itemId, request);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@RequestHeader("userId") Integer userId,
                                           @PathVariable Integer itemId) {
        service.deleteItem(userId, itemId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{listId}/items/check-all")
    public ResponseEntity<Void> checkAll(@RequestHeader("userId") Integer userId,
                                         @PathVariable Integer listId,
                                         @Valid @RequestBody CheckAllItemsRequestDto request) {
        service.checkAll(userId, listId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{listId}/items/completed")
    public ResponseEntity<Void> clearCompleted(@RequestHeader("userId") Integer userId,
                                               @PathVariable Integer listId) {
        service.clearCompleted(userId, listId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{listId}/items/from-recipe")
    @ResponseStatus(HttpStatus.CREATED)
    public List<ShoppingListItemResponseDto> addItemsFromRecipe(@RequestHeader("userId") Integer userId,
                                                                @PathVariable Integer listId,
                                                                @Valid @RequestBody AddItemsFromRecipeRequestDto request) {
        return service.addItemsFromRecipe(userId, listId, request);
    }

    @PostMapping("/merge")
    public ShoppingListResponseDto mergeLists(@RequestHeader("userId") Integer userId,
                                              @Valid @RequestBody MergeShoppingListsRequestDto request) {
        return service.mergeLists(userId, request);
    }
}
