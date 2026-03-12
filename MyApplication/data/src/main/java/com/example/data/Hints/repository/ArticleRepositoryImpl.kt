package com.example.data.Hints.repository

import com.example.data.Hints.datasource.local.ArticleLocalDataSource
import com.example.domain.Hints.model.Article
import com.example.domain.Hints.model.ArticleContent
import com.example.domain.Hints.model.Category
import com.example.domain.Hints.model.Favorite
import com.example.domain.Hints.model.Like
import com.example.domain.Hints.repository.ArticleRepository

class ArticleRepositoryImpl(
    private val localDataSource: ArticleLocalDataSource,
    private val userId: Int = 1
) : ArticleRepository {

    override suspend fun getArticles(): List<Article> {
        val articleEntities = localDataSource.getArticles()
        return articleEntities.map { entity ->
            val category = localDataSource.getCategories()
                .find { it.id == entity.categoryId }
                ?: throw IllegalStateException("Category not found for article ${entity.id}")
            val likesCount = localDataSource.getLikesCount(entity.id)
            entity.toDomain(category, likesCount)
        }
    }

    override suspend fun getArticleContent(articleId: Int): ArticleContent {
        val contentEntity = localDataSource.getArticleContent(articleId)
            ?: throw IllegalStateException("Content not found for article $articleId")
        return contentEntity.toDomain()
    }

    override suspend fun getArticlesByCategory(categoryId: Int): List<Article> {
        val articleEntities = localDataSource.getArticlesByCategory(categoryId)
        return articleEntities.map { entity ->
            val category = localDataSource.getCategories()
                .find { it.id == entity.categoryId }
                ?: throw IllegalStateException("Category not found for article ${entity.id}")
            val likesCount = localDataSource.getLikesCount(entity.id)
            entity.toDomain(category, likesCount)
        }
    }

    override suspend fun searchArticles(query: String): List<Article> {
        val articleEntities = localDataSource.getArticles().filter { article ->
            article.title.contains(query, ignoreCase = true) ||
                    article.mainWords.any { it.contains(query, ignoreCase = true) }
        }
        return articleEntities.map { entity ->
            val category = localDataSource.getCategories()
                .find { it.id == entity.categoryId }
                ?: throw IllegalStateException("Category not found for article ${entity.id}")
            val likesCount = localDataSource.getLikesCount(entity.id)
            entity.toDomain(category, likesCount)
        }
    }

    override suspend fun getFavoriteArticles(userId: Int): List<Article> {
        val favoriteEntities = localDataSource.getFavorites(userId)
        val favoriteArticleIds = favoriteEntities.map { it.articleId }

        return localDataSource.getArticles()
            .filter { it.id in favoriteArticleIds }
            .map { entity ->
                val category = localDataSource.getCategories()
                    .find { it.id == entity.categoryId }
                    ?: throw IllegalStateException("Category not found for article ${entity.id}")
                val likesCount = localDataSource.getLikesCount(entity.id)
                entity.toDomain(category, likesCount)
            }
    }

    override suspend fun addToFavorites(userId: Int, articleId: Int): Favorite {
        val favoriteEntity = localDataSource.addFavorite(userId, articleId)
        return favoriteEntity.toDomain()
    }

    override suspend fun removeFromFavorites(userId: Int, articleId: Int): Boolean =
        localDataSource.removeFavorite(userId, articleId)

    override suspend fun isArticleFavorite(userId: Int, articleId: Int): Boolean =
        localDataSource.isFavorite(userId, articleId)

    override suspend fun getLikedArticles(userId: Int): List<Article> {
        val likedEntities = localDataSource.getUserLikes(userId)
        val likedArticleIds = likedEntities.map { it.articleId }

        return localDataSource.getArticles()
            .filter { it.id in likedArticleIds }
            .map { entity ->
                val category = localDataSource.getCategories()
                    .find { it.id == entity.categoryId }
                    ?: throw IllegalStateException("Category not found for article ${entity.id}")
                val likesCount = localDataSource.getLikesCount(entity.id)
                entity.toDomain(category, likesCount)
            }
    }

    override suspend fun addLike(userId: Int, articleId: Int): Like {
        val likeEntity = localDataSource.addLike(userId, articleId)
        return likeEntity.toDomain()
    }

    override suspend fun removeLike(userId: Int, articleId: Int): Boolean =
        localDataSource.removeLike(userId, articleId)

    override suspend fun isArticleLiked(userId: Int, articleId: Int): Boolean =
        localDataSource.isLiked(userId, articleId)

    override suspend fun getLikesCount(articleId: Int): Int =
        localDataSource.getLikesCount(articleId)

    override suspend fun getCategories(): List<Category> {
        return localDataSource.getCategories().map { it.toDomain() }
    }
}