package com.example.domain.Hints.use_case

import com.example.domain.Hints.repository.ArticleRepository

class RemoveFromFavoriteUseCase(
    private val repository: ArticleRepository
) {
    suspend operator fun invoke(userId: Int, articleId: Int): Boolean {
        return repository.removeFromFavorites(userId, articleId)
    }
}