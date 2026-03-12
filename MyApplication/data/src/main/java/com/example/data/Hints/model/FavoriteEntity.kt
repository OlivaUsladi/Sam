package com.example.data.Hints.model

import com.example.domain.Hints.model.Favorite

data class FavoriteEntity(
    val id: Int,
    val userId: Int,
    val articleId: Int
) {
    fun toDomain(): Favorite {
        return Favorite(
            userId = userId,
            articleId = articleId
        )
    }
}