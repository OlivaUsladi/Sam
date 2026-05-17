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
import com.example.backend.security.AuthenticatedUser;
import com.example.backend.service.ShoppingListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shopping-lists")
@RequiredArgsConstructor
public class ShoppingListController {

    private final ShoppingListService service;


    @GetMapping
    public List<ShoppingListResponseDto> getLists(@AuthenticationPrincipal AuthenticatedUser principal) {
        return service.getLists(principal.id());
    }

    @GetMapping("/{listId}")
    public ShoppingListResponseDto getList(@AuthenticationPrincipal AuthenticatedUser principal,
                                           @PathVariable Integer listId) {
        return service.getList(principal.id(), listId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingListResponseDto createList(@AuthenticationPrincipal AuthenticatedUser principal,
                                              @Valid @RequestBody CreateShoppingListRequestDto request) {
        return service.createList(principal.id(), request);
    }

    @PatchMapping("/{listId}")
    public ShoppingListResponseDto renameList(@AuthenticationPrincipal AuthenticatedUser principal,
                                              @PathVariable Integer listId,
                                              @Valid @RequestBody RenameShoppingListRequestDto request) {
        return service.renameList(principal.id(), listId, request);
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<Void> deleteList(@AuthenticationPrincipal AuthenticatedUser principal,
                                           @PathVariable Integer listId) {
        service.deleteList(principal.id(), listId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{listId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingListItemResponseDto addItem(@AuthenticationPrincipal AuthenticatedUser principal,
                                               @PathVariable Integer listId,
                                               @Valid @RequestBody AddShoppingListItemRequestDto request) {
        return service.addItem(principal.id(), listId, request);
    }

    @PatchMapping("/items/{itemId}")
    public ShoppingListItemResponseDto updateItem(@AuthenticationPrincipal AuthenticatedUser principal,
                                                  @PathVariable Integer itemId,
                                                  @Valid @RequestBody UpdateShoppingListItemRequestDto request) {
        return service.updateItem(principal.id(), itemId, request);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@AuthenticationPrincipal AuthenticatedUser principal,
                                           @PathVariable Integer itemId) {
        service.deleteItem(principal.id(), itemId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{listId}/items/check-all")
    public ResponseEntity<Void> checkAll(@AuthenticationPrincipal AuthenticatedUser principal,
                                         @PathVariable Integer listId,
                                         @Valid @RequestBody CheckAllItemsRequestDto request) {
        service.checkAll(principal.id(), listId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{listId}/items/completed")
    public ResponseEntity<Void> clearCompleted(@AuthenticationPrincipal AuthenticatedUser principal,
                                               @PathVariable Integer listId) {
        service.clearCompleted(principal.id(), listId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{listId}/items/from-recipe")
    @ResponseStatus(HttpStatus.CREATED)
    public List<ShoppingListItemResponseDto> addItemsFromRecipe(@AuthenticationPrincipal AuthenticatedUser principal,
                                                                @PathVariable Integer listId,
                                                                @Valid @RequestBody AddItemsFromRecipeRequestDto request) {
        return service.addItemsFromRecipe(principal.id(), listId, request);
    }

    @PostMapping("/merge")
    public ShoppingListResponseDto mergeLists(@AuthenticationPrincipal AuthenticatedUser principal,
                                              @Valid @RequestBody MergeShoppingListsRequestDto request) {
        return service.mergeLists(principal.id(), request);
    }
}
