package com.example.domain.Hints.repository

import com.example.domain.Hints.model.Article
import com.example.domain.Hints.model.ArticleContent
import com.example.domain.Hints.model.Category
import com.example.domain.Hints.model.Favorite
import com.example.domain.Hints.model.Like

interface ArticleRepository {
    suspend fun getArticles(): List<Article>
    suspend fun getArticleContent(articleId: Int): ArticleContent
    suspend fun getArticlesByCategory(categoryId: Int): List<Article>
    //Поиск статей по названию и ключевым словам
    suspend fun searchArticles(query: String): List<Article>

    // Методы для избранного
    suspend fun getFavoriteArticles(userId: Int): List<Article>
    suspend fun addToFavorites(userId: Int, articleId: Int): Favorite
    suspend fun removeFromFavorites(userId: Int, articleId: Int): Boolean
    suspend fun isArticleFavorite(userId: Int, articleId: Int): Boolean

    // Методы для лайков
    suspend fun getLikedArticles(userId: Int): List<Article>
    suspend fun addLike(userId: Int, articleId: Int): Like
    suspend fun removeLike(userId: Int, articleId: Int): Boolean
    suspend fun isArticleLiked(userId: Int, articleId: Int): Boolean
    suspend fun getLikesCount(articleId: Int): Int

    suspend fun getCategories(): List<Category>
}