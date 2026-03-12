package com.example.domain.Hints.use_case

import com.example.domain.Hints.model.Article
import com.example.domain.Hints.model.ArticleContent
import com.example.domain.Hints.repository.ArticleRepository

class GetArticleContentUseCase (
    private val articleRepository: ArticleRepository
    )
    {
        suspend operator fun invoke(articleId: Int): ArticleContent {
            return articleRepository.getArticleContent(articleId)
        }
}