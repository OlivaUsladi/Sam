package com.example.domain.Hints.use_case

import com.example.domain.Hints.model.Like
import com.example.domain.Hints.repository.ArticleRepository

class AddLikeUseCase (
    private val repository: ArticleRepository)
{
    suspend operator fun invoke(userId: Int, articleId: Int): Like {
        return repository.addLike(userId, articleId)
    }
}