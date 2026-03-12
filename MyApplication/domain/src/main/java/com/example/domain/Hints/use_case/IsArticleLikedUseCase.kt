package com.example.domain.Hints.use_case

import com.example.domain.Hints.repository.ArticleRepository

class IsArticleLikedUseCase(
    private val repository: ArticleRepository
) {
    suspend operator fun invoke(userId: Int, articleId: Int): Boolean {
        return repository.isArticleLiked(userId, articleId)
    }
}