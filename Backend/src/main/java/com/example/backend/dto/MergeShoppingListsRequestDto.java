package com.example.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MergeShoppingListsRequestDto(
        @NotNull
        Integer targetListId,
        @NotEmpty
        List<Integer> sourceListIds
) {
}
