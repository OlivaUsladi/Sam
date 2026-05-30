package com.example.domain.Hints.use_case

import com.example.domain.Hints.model.Article
import com.example.domain.Hints.repository.ArticleRepository

class GetArticleUseCase(
    private val repository: ArticleRepository
) {
    suspend operator fun invoke(articleId: Int): Article {
        return repository.getArticle(articleId)
    }
}