package com.example.data.Hints.datasource.local

import com.example.data.Hints.model.*

interface ArticleLocalDataSource {
    // Статьи и категории
    suspend fun getArticles(): List<ArticleEntity>
    suspend fun getArticleById(articleId: Int): ArticleEntity?
    suspend fun getArticlesByCategory(categoryId: Int): List<ArticleEntity>
    suspend fun getCategories(): List<CategoryEntity>

    // Контент и чеклисты
    suspend fun getArticleContent(articleId: Int): ArticleContentEntity?

    // Избранное
    suspend fun getFavorites(userId: Int): List<FavoriteEntity>
    suspend fun addFavorite(userId: Int, articleId: Int): FavoriteEntity
    suspend fun removeFavorite(userId: Int, articleId: Int): Boolean
    suspend fun isFavorite(userId: Int, articleId: Int): Boolean

    // Лайки
    suspend fun getLikes(articleId: Int): List<LikeEntity>
    suspend fun getUserLikes(userId: Int): List<LikeEntity>
    suspend fun addLike(userId: Int, articleId: Int): LikeEntity
    suspend fun removeLike(userId: Int, articleId: Int): Boolean
    suspend fun isLiked(userId: Int, articleId: Int): Boolean
    suspend fun getLikesCount(articleId: Int): Int
}