package com.example.backend.mapper;

import com.example.backend.dto.*;
import com.example.backend.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecipeMapper {

    //это для преобразования между Java-объектами и JSON-данными
    private final ObjectMapper objectMapper;

    public RecipeMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public RecipeResponseDto toResponseDto(RecipeEntity entity) {
        if (entity == null) return null;

        return RecipeResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .author(entity.getAuthor())
                .previewImageUrl(entity.getPreviewImageUrl())
                .cookingTimeMinutes(entity.getCookingTimeMinutes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .likesCount(entity.getLikesCount())
                .isFavorite(false)
                .isLiked(false)
                .categories(toCategoryDtoList(entity.getCategories()))
                .build();
    }

    public RecipeDetailResponseDto toDetailResponseDto(RecipeEntity entity) {
        if (entity == null) return null;

        return RecipeDetailResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .author(entity.getAuthor())
                .previewImageUrl(entity.getPreviewImageUrl())
                .cookingTimeMinutes(entity.getCookingTimeMinutes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .likesCount(entity.getLikesCount())
                .isFavorite(false)
                .isLiked(false)
                .categories(toCategoryDtoList(entity.getCategories()))
                .ingredients(toIngredientDtoList(entity.getRecipeGroceries()))
                .cookingSteps(parseCookingSteps(entity.getContent()))
                .tips(entity.getContent() != null ? entity.getContent().getTips() : null)
                .build();
    }

    public List<RecipeResponseDto> toResponseDtoList(List<RecipeEntity> entities) {
        return entities.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    private List<CategoryResponseDto> toCategoryDtoList(List<CategoryEntity> categories) {
        return categories.stream()
                .map(cat -> CategoryResponseDto.builder()
                        .id(cat.getId())
                        .name(cat.getName())
                        .description(cat.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    private List<RecipeIngredientResponseDto> toIngredientDtoList(List<RecipeGroceryCrossEntity> crossEntities) {
        return crossEntities.stream()
                .map(cross -> RecipeIngredientResponseDto.builder()
                        .groceryItem(GroceryItemResponseDto.builder()
                                .id(cross.getGroceryItem().getId())
                                .groceryId(cross.getGroceryItem().getGrocery().getId())
                                .name(cross.getGroceryItem().getName())
                                .defaultUnit(cross.getGroceryItem().getUnit())
                                .build())
                        .amount(cross.getAmount())
                        .unit(cross.getUnit())
                        .build())
                .collect(Collectors.toList());
    }

    private List<ContentBlockDto> parseCookingSteps(RecipeContentEntity content) {
        if (content == null || content.getCookingSteps() == null || content.getCookingSteps().isBlank()) {
            return List.of();
        }

        try {
            return objectMapper.readValue(content.getCookingSteps(), List.class);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}