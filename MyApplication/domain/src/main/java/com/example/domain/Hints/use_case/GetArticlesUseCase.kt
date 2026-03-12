package com.example.domain.Hints.use_case

import com.example.domain.Hints.model.Article
import com.example.domain.Hints.repository.ArticleRepository

class GetArticlesUseCase(
    private val repository: ArticleRepository
) {
    suspend operator fun invoke(): List<Article> {
        return repository.getArticles()
    }
}