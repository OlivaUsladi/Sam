package com.example.domain.Hints.use_case

import com.example.domain.Hints.model.Favorite
import com.example.domain.Hints.repository.ArticleRepository

class AddToFavoriteUseCase(
    private val repository: ArticleRepository
) {
    suspend operator fun invoke(userId: Int, articleId: Int): Favorite {
        return repository.addToFavorites(userId, articleId)
    }
}