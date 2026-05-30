package com.example.data.Hints.datasource.remote

import com.example.data.Hints.datasource.remote.api.ArticleApiService
import com.example.data.Hints.datasource.remote.dto.ArticleCategoryDto
import com.example.data.Hints.datasource.remote.dto.ArticleDetailResponseDto
import com.example.data.Hints.datasource.remote.dto.ArticleResponseDto
import com.example.domain.Hints.model.Article

class ArticleRemoteDataSource(
    private val apiService: ArticleApiService
) {

    suspend fun getCategories(): List<ArticleCategoryDto> =
        apiService.getCategories()

    suspend fun getArticles(userId: Int): List<ArticleResponseDto> =
        apiService.getAll(userId)

    suspend fun getArticle(articleId: Int, userId: Int): ArticleResponseDto =
        apiService.getArticle(
            articleId,
            userId)

    suspend fun getArticleById(articleId: Int, userId: Int): ArticleDetailResponseDto =
        apiService.getById(articleId, userId)

    suspend fun getArticlesByCategory(categoryId: Int, userId: Int): List<ArticleResponseDto> =
        apiService.getByCategory(categoryId, userId)

    suspend fun searchArticles(query: String, userId: Int): List<ArticleResponseDto> =
        apiService.search(query, userId)

    suspend fun getFavourites(userId: Int): List<ArticleResponseDto> =
        apiService.getFavourites(userId)

    suspend fun isFavourite(articleId: Int, userId: Int): Boolean {
        val response = apiService.isFavourite(articleId, userId)
        return response.isSuccessful && response.body() == true
    }

    suspend fun addToFavourites(articleId: Int, userId: Int) {
        val response = apiService.addToFavourites(articleId, userId)
        check(response.isSuccessful) { "Не удалось добавить в избранное: HTTP ${response.code()}" }
    }

    suspend fun removeFromFavourites(articleId: Int, userId: Int) {
        val response = apiService.removeFromFavourites(articleId, userId)
        check(response.isSuccessful) { "Не удалось удалить из избранного: HTTP ${response.code()}" }
    }

    suspend fun getLiked(userId: Int): List<ArticleResponseDto> =
        apiService.getLiked(userId)

    suspend fun isLiked(articleId: Int, userId: Int): Boolean {
        val response = apiService.isLiked(articleId, userId)
        return response.isSuccessful && response.body() == true
    }

    suspend fun getLikesCount(articleId: Int): Int {
        val response = apiService.getLikesCount(articleId)
        return if (response.isSuccessful) (response.body() ?: 0L).toInt() else 0
    }

    suspend fun addLike(articleId: Int, userId: Int) {
        val response = apiService.addLike(articleId, userId)
        check(response.isSuccessful) { "Не удалось поставить лайк: HTTP ${response.code()}" }
    }

    suspend fun removeLike(articleId: Int, userId: Int) {
        val response = apiService.removeLike(articleId, userId)
        check(response.isSuccessful) { "Не удалось убрать лайк: HTTP ${response.code()}" }
    }
}
