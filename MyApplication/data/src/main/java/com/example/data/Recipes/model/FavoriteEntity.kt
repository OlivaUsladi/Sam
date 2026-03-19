package com.example.data.Recipes.model

import com.example.domain.Recipes.model.Favourite
import java.time.LocalDateTime

data class FavoriteEntity(
    val id: Int,
    val userId: Int,
    val recipeId: Int,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    fun toDomain(): Favourite {
        return Favourite(
            userId = userId,
            recipeId = recipeId
        )
    }

    companion object {
        fun fromDomain(favorite: Favourite): FavoriteEntity {
            return FavoriteEntity(
                id = 0,
                userId = favorite.userId,
                recipeId = favorite.recipeId
            )
        }
    }
}