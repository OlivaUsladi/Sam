package com.example.data.Recipes.model

import com.example.domain.Recipes.model.Like
import java.time.LocalDateTime

data class LikeEntity(
    val id: Int,
    val userId: Int,
    val recipeId: Int,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    fun toDomain(): Like {
        return Like(
            userId = userId,
            recipeId = recipeId
        )
    }

}