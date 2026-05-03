package com.example.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RecipeDetailResponseDto {
    private Integer id;
    private String title;
    private String description;
    private String author;
    private String previewImageUrl;
    private Integer cookingTimeMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer likesCount;
    private Boolean isFavorite;
    private Boolean isLiked;
    private List<CategoryResponseDto> categories;
    private List<RecipeIngredientResponseDto> ingredients;
    private List<ContentBlockDto> cookingSteps;
    private String tips;
}