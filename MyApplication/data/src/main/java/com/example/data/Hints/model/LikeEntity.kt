package com.example.data.Hints.model

import com.example.domain.Hints.model.Like

data class LikeEntity(
    val id: Int,
    val userId: Int,
    val articleId: Int
) {
    fun toDomain(): Like {
        return Like(
            userId = userId,
            articleId = articleId
        )
    }
}