package com.example.data.Recipes.remote.dto

data class RecipeResponseDto(
    val id: Int,
    val title: String,
    val description: String?,
    val author: String,
    val previewImageUrl: String?,
    val cookingTimeMinutes: Int,
    val createdAt: String,
    val updatedAt: String,
    val likesCount: Int,
    val isFavorite: Boolean,
    val isLiked: Boolean,
    val categories: List<CategoryDto>
)