package com.example.domain.Recipes.model

import java.time.LocalDateTime

data class Recipe(
    val id: Int,
    val title: String,
    val description: String?,
    val categories: List<Category>,
    val groceries: List<Grocery>,
    val groceryItems: List<GroceryItem> = emptyList(),
    val author: String,
    val previewImageUrl: String?,
    val cookingTimeMinutes: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val likesCount: Int = 0,
    val isFavorite: Boolean = false,
    val isLiked: Boolean = false
)
