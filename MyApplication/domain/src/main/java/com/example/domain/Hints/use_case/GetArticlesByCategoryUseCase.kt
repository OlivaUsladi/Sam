package com.example.domain.Hints.use_case

import com.example.domain.Hints.model.Article
import com.example.domain.Hints.repository.ArticleRepository

class GetArticlesByCategoryUseCase(
    private val repository: ArticleRepository
) {
    suspend operator fun invoke(categoryId: Int): List<Article> {
        return repository.getArticlesByCategory(categoryId)
    }
}