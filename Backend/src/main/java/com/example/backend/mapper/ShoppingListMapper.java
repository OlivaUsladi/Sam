package com.example.backend.mapper;

import com.example.backend.dto.ShoppingListItemResponseDto;
import com.example.backend.dto.ShoppingListResponseDto;
import com.example.backend.entity.ShoppingListEntity;
import com.example.backend.entity.ShoppingListItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShoppingListMapper {

    public ShoppingListResponseDto toDto(ShoppingListEntity list, List<ShoppingListItemEntity> items) {
        return new ShoppingListResponseDto(
                list.getId(),
                list.getUserId(),
                list.getName(),
                list.getCreatedAt(),
                isCompleted(items),
                items.stream().map(this::toDto).toList()
        );
    }

    public ShoppingListItemResponseDto toDto(ShoppingListItemEntity item) {
        return new ShoppingListItemResponseDto(
                item.getId(),
                item.getDescription(),
                Boolean.TRUE.equals(item.getIsChecked()),
                item.getQuantity(),
                item.getUnit()
        );
    }


    private static boolean isCompleted(List<ShoppingListItemEntity> items) {
        if (items.isEmpty()) return false;
        for (ShoppingListItemEntity item : items) {
            if (!Boolean.TRUE.equals(item.getIsChecked())) return false;
        }
        return true;
    }
}
