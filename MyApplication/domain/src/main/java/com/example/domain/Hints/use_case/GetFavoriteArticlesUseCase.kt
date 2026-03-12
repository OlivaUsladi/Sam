package com.example.domain.Hints.use_case

import com.example.domain.Hints.model.Article
import com.example.domain.Hints.repository.ArticleRepository

class GetFavoriteArticlesUseCase(
    private val repository: ArticleRepository
) {
    suspend operator fun invoke(userId: Int): List<Article> {
        return repository.getFavoriteArticles(userId)
    }
}