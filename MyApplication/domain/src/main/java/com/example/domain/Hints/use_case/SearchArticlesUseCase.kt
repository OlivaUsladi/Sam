package com.example.domain.Hints.use_case

import com.example.domain.Hints.model.Article
import com.example.domain.Hints.repository.ArticleRepository

class SearchArticlesUseCase(
    private val repository: ArticleRepository
) {
    suspend operator fun invoke(query: String): List<Article> {
        return repository.searchArticles(query)
    }
}