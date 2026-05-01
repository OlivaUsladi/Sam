package com.example.data.Recipes.model

import com.example.domain.Recipes.model.Recipe
import com.example.domain.Recipes.model.RecipeIngredient
import java.time.LocalDateTime

data class RecipeEntity(
    val id: Int,
    val title: String,
    val description: String?,
    val author: String,
    val previewImageUrl: String?,
    val cookingTimeMinutes: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val likesCount: Int = 0
) {
    fun toDomain(
        categories: List<CategoryEntity> = emptyList(),
        ingredients: List<RecipeIngredient> = emptyList(),
        isFavorite: Boolean = false,
        isLiked: Boolean = false
    ): Recipe {
        return Recipe(
            id = id,
            title = title,
            description = description,
            categories = categories.map { it.toDomain() },
            ingredients = ingredients,
            author = author,
            previewImageUrl = previewImageUrl,
            cookingTimeMinutes = cookingTimeMinutes,
            createdAt = createdAt,
            updatedAt = updatedAt,
            likesCount = likesCount,
            isFavorite = isFavorite,
            isLiked = isLiked
        )
    }
}