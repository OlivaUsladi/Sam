package com.example.domain.Hints.use_case

import com.example.domain.Hints.repository.ArticleRepository

class GetLikesCountUseCase (private val repository: ArticleRepository) {
    suspend operator fun invoke(articleId: Int): Int {
        return repository.getLikesCount(articleId)
    }
}