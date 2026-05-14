package com.example.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ShoppingListResponseDto(
        Integer id,
        Integer userId,
        String name,
        LocalDateTime createdAt,
        Boolean isCompleted,
        List<ShoppingListItemResponseDto> items
) {
}
