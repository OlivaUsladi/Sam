package com.example.domain.Hints.repository

import com.example.domain.Hints.model.Article
import com.example.domain.Hints.model.ArticleContent

interface ArticleRepository {
    suspend fun getArticlesByCategory(categoryId: String): List<Article>
    suspend fun getArticleContent(articleId: Int): ArticleContent
    suspend fun getFavorites(): List<Article>
    suspend fun getArticles(): List<Article>
    suspend fun getArticlesByName(name: String): List<Article>
    suspend fun getArticleByMainWord(mainWord: String): List<Article>

    suspend fun toggleFavorite(articleId: Int): Boolean
    suspend fun toggleLike(articleId: Int): Boolean

    suspend fun markArticleAsRead(articleId: Int, percentage: Float)
}