package com.example.data.Hints.repository

import com.example.data.Hints.datasource.remote.ArticleRemoteDataSource
import com.example.data.Hints.datasource.remote.mapper.ArticleNetworkMapper
import com.example.domain.Hints.model.Article
import com.example.domain.Hints.model.ArticleContent
import com.example.domain.Hints.model.Category
import com.example.domain.Hints.model.Favorite
import com.example.domain.Hints.model.Like
import com.example.domain.Hints.repository.ArticleRepository

class ArticleRepositoryImpl(
    private val remoteDataSource: ArticleRemoteDataSource,
    private val userId: Int = 1
) : ArticleRepository {

    override suspend fun getArticles(): List<Article> =
        remoteDataSource.getArticles(userId)
            .map { ArticleNetworkMapper.mapToDomain(it) }

    override suspend fun getArticleContent(articleId: Int): ArticleContent {
        val detail = remoteDataSource.getArticleById(articleId, userId)
        return ArticleNetworkMapper.mapDetailToContent(detail)
    }

    override suspend fun getArticlesByCategory(categoryId: Int): List<Article> =
        remoteDataSource.getArticlesByCategory(categoryId, userId)
            .map { ArticleNetworkMapper.mapToDomain(it) }

    override suspend fun searchArticles(query: String): List<Article> =
        remoteDataSource.searchArticles(query, userId)
            .map { ArticleNetworkMapper.mapToDomain(it) }

    override suspend fun getFavoriteArticles(userId: Int): List<Article> =
        remoteDataSource.getFavourites(userId)
            .map { ArticleNetworkMapper.mapToDomain(it) }

    override suspend fun addToFavorites(userId: Int, articleId: Int): Favorite {
        remoteDataSource.addToFavourites(articleId, userId)
        return Favorite(userId = userId, articleId = articleId)
    }

    override suspend fun removeFromFavorites(userId: Int, articleId: Int): Boolean {
        remoteDataSource.removeFromFavourites(articleId, userId)
        return true
    }

    override suspend fun isArticleFavorite(userId: Int, articleId: Int): Boolean =
        remoteDataSource.isFavourite(articleId, userId)

    override suspend fun getLikedArticles(userId: Int): List<Article> =
        remoteDataSource.getLiked(userId)
            .map { ArticleNetworkMapper.mapToDomain(it) }

    override suspend fun addLike(userId: Int, articleId: Int): Like {
        remoteDataSource.addLike(articleId, userId)
        return Like(userId = userId, articleId = articleId)
    }

    override suspend fun removeLike(userId: Int, articleId: Int): Boolean {
        remoteDataSource.removeLike(articleId, userId)
        return true
    }

    override suspend fun isArticleLiked(userId: Int, articleId: Int): Boolean =
        remoteDataSource.isLiked(articleId, userId)

    override suspend fun getLikesCount(articleId: Int): Int =
        remoteDataSource.getLikesCount(articleId)

    override suspend fun getCategories(): List<Category> =
        remoteDataSource.getCategories()
            .map { ArticleNetworkMapper.mapToDomain(it) }
}
