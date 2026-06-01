package com.example.data.Hints.repository

import com.example.data.Hints.datasource.local.ArticleLocalDataSource
import com.example.data.Hints.datasource.remote.ArticleRemoteDataSource
import com.example.data.Hints.datasource.remote.mapper.ArticleNetworkMapper
import com.example.data.Hints.model.ArticleEntity
import com.example.data.Hints.model.CategoryEntity
import com.example.domain.Hints.model.Article
import com.example.domain.Hints.model.ArticleContent
import com.example.domain.Hints.model.Category
import com.example.domain.Hints.model.Favorite
import com.example.domain.Hints.model.Like
import com.example.domain.Hints.repository.ArticleRepository


class ArticleRepositoryImpl(
    private val remoteDataSource: ArticleRemoteDataSource,
    private val localDataSource: ArticleLocalDataSource,
    private val userId: Int = 1,
) : ArticleRepository {

    private fun ArticleEntity.toDomain(
        category: Category,
        likesCount: Int,
        isFavorite: Boolean,
        isLiked: Boolean,
    ): Article = Article(
        id = id,
        title = title,
        category = category,
        mainWords = mainWords,
        author = author,
        imageUrl = imageUrl,
        createdAt = createdAt,
        updatedAt = updatedAt,
        likesCount = likesCount,
        isFavorite = isFavorite,
        isLiked = isLiked,
    )

    private suspend fun localArticles(filter: (ArticleEntity) -> Boolean = { true }): List<Article> {
        val categories: Map<Int, CategoryEntity> =
            localDataSource.getCategories().associateBy { it.id }
        return localDataSource.getArticles()
            .filter(filter)
            .map { entity ->
                val cat = categories[entity.categoryId]?.toDomain() ?: Category(0, "—", null)
                entity.toDomain(
                    category = cat,
                    likesCount = localDataSource.getLikesCount(entity.id),
                    isFavorite = localDataSource.isFavorite(userId, entity.id),
                    isLiked = localDataSource.isLiked(userId, entity.id),
                )
            }
    }

    override suspend fun getArticles(): List<Article> = try {
        remoteDataSource.getArticles(userId).map { ArticleNetworkMapper.mapToDomain(it) }
    } catch (e: Exception) {
        localArticles()
    }

    override suspend fun getArticleContent(articleId: Int): ArticleContent = try {
        val detail = remoteDataSource.getArticleById(articleId, userId)
        ArticleNetworkMapper.mapDetailToContent(detail)
    } catch (e: Exception) {
        localDataSource.getArticleContent(articleId)?.toDomain()
            ?: ArticleContent(articleId = articleId, blocks = emptyList(), checklist = "")
    }

    override suspend fun getArticle(articleId: Int): Article = try {
        ArticleNetworkMapper.mapToDomain(remoteDataSource.getArticle(articleId, userId))
    } catch (e: Exception) {
        val entity = localDataSource.getArticleById(articleId)
            ?: error("Статья недоступна в офлайн-режиме")
        val categories = localDataSource.getCategories().associateBy { it.id }
        entity.toDomain(
            category = categories[entity.categoryId]?.toDomain() ?: Category(0, "—", null),
            likesCount = localDataSource.getLikesCount(articleId),
            isFavorite = localDataSource.isFavorite(userId, articleId),
            isLiked = localDataSource.isLiked(userId, articleId),
        )
    }

    override suspend fun getArticlesByCategory(categoryId: Int): List<Article> = try {
        remoteDataSource.getArticlesByCategory(categoryId, userId)
            .map { ArticleNetworkMapper.mapToDomain(it) }
    } catch (e: Exception) {
        localArticles { it.categoryId == categoryId }
    }

    override suspend fun searchArticles(query: String): List<Article> = try {
        remoteDataSource.searchArticles(query, userId)
            .map { ArticleNetworkMapper.mapToDomain(it) }
    } catch (e: Exception) {
        val q = query.trim().lowercase()
        localArticles { entity ->
            entity.title.lowercase().contains(q) ||
                    entity.mainWords.any { it.lowercase().contains(q) } ||
                    entity.author.lowercase().contains(q)
        }
    }

    override suspend fun getFavoriteArticles(userId: Int): List<Article> = try {
        remoteDataSource.getFavourites(userId).map { ArticleNetworkMapper.mapToDomain(it) }
    } catch (e: Exception) {
        val favIds = localDataSource.getFavorites(userId).map { it.articleId }.toSet()
        localArticles { it.id in favIds }
    }

    override suspend fun addToFavorites(userId: Int, articleId: Int): Favorite {
        runCatching { remoteDataSource.addToFavourites(articleId, userId) }
            .onFailure { localDataSource.addFavorite(userId, articleId) }
        return Favorite(userId = userId, articleId = articleId)
    }

    override suspend fun removeFromFavorites(userId: Int, articleId: Int): Boolean {
        runCatching { remoteDataSource.removeFromFavourites(articleId, userId) }
            .onFailure { localDataSource.removeFavorite(userId, articleId) }
        return true
    }

    override suspend fun isArticleFavorite(userId: Int, articleId: Int): Boolean = try {
        remoteDataSource.isFavourite(articleId, userId)
    } catch (e: Exception) {
        localDataSource.isFavorite(userId, articleId)
    }

    override suspend fun getLikedArticles(userId: Int): List<Article> = try {
        remoteDataSource.getLiked(userId).map { ArticleNetworkMapper.mapToDomain(it) }
    } catch (e: Exception) {
        val likedIds = localDataSource.getUserLikes(userId).map { it.articleId }.toSet()
        localArticles { it.id in likedIds }
    }

    override suspend fun addLike(userId: Int, articleId: Int): Like {
        runCatching { remoteDataSource.addLike(articleId, userId) }
            .onFailure { localDataSource.addLike(userId, articleId) }
        return Like(userId = userId, articleId = articleId)
    }

    override suspend fun removeLike(userId: Int, articleId: Int): Boolean {
        runCatching { remoteDataSource.removeLike(articleId, userId) }
            .onFailure { localDataSource.removeLike(userId, articleId) }
        return true
    }

    override suspend fun isArticleLiked(userId: Int, articleId: Int): Boolean = try {
        remoteDataSource.isLiked(articleId, userId)
    } catch (e: Exception) {
        localDataSource.isLiked(userId, articleId)
    }

    override suspend fun getLikesCount(articleId: Int): Int = try {
        remoteDataSource.getLikesCount(articleId)
    } catch (e: Exception) {
        localDataSource.getLikesCount(articleId)
    }

    override suspend fun getCategories(): List<Category> = try {
        remoteDataSource.getCategories().map { ArticleNetworkMapper.mapToDomain(it) }
    } catch (e: Exception) {
        localDataSource.getCategories().map { it.toDomain() }
    }
}
