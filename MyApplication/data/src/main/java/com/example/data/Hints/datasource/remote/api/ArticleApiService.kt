package com.example.data.Hints.datasource.remote.api

import com.example.data.Hints.datasource.remote.dto.ArticleCategoryDto
import com.example.data.Hints.datasource.remote.dto.ArticleDetailResponseDto
import com.example.data.Hints.datasource.remote.dto.ArticleResponseDto
import retrofit2.Response
import retrofit2.http.*

interface ArticleApiService {

    @GET("/api/article-categories")
    suspend fun getCategories(): List<ArticleCategoryDto>

    @GET("/api/articles")
    suspend fun getAll(@Header("userId") userId: Int): List<ArticleResponseDto>

    @GET("/api/articles/{id}")
    suspend fun getById(
        @Path("id") id: Int,
        @Header("userId") userId: Int
    ): ArticleDetailResponseDto

    @GET("/api/articles/search")
    suspend fun search(
        @Query("query") query: String,
        @Header("userId") userId: Int
    ): List<ArticleResponseDto>

    @GET("/api/articles/category/{categoryId}")
    suspend fun getByCategory(
        @Path("categoryId") categoryId: Int,
        @Header("userId") userId: Int
    ): List<ArticleResponseDto>

    @GET("/api/favourite-articles")
    suspend fun getFavourites(@Header("userId") userId: Int): List<ArticleResponseDto>

    @GET("/api/favourite-articles/{articleId}")
    suspend fun isFavourite(
        @Path("articleId") articleId: Int,
        @Header("userId") userId: Int
    ): Response<Boolean>

    @POST("/api/favourite-articles/{articleId}")
    suspend fun addToFavourites(
        @Path("articleId") articleId: Int,
        @Header("userId") userId: Int
    ): Response<Unit>

    @DELETE("/api/favourite-articles/{articleId}")
    suspend fun removeFromFavourites(
        @Path("articleId") articleId: Int,
        @Header("userId") userId: Int
    ): Response<Unit>

    @GET("/api/likes-articles")
    suspend fun getLiked(@Header("userId") userId: Int): List<ArticleResponseDto>

    @GET("/api/likes-articles/{articleId}")
    suspend fun isLiked(
        @Path("articleId") articleId: Int,
        @Header("userId") userId: Int
    ): Response<Boolean>

    @GET("/api/likes-articles/{articleId}/count")
    suspend fun getLikesCount(@Path("articleId") articleId: Int): Response<Long>

    @POST("/api/likes-articles/{articleId}")
    suspend fun addLike(
        @Path("articleId") articleId: Int,
        @Header("userId") userId: Int
    ): Response<Unit>

    @DELETE("/api/likes-articles/{articleId}")
    suspend fun removeLike(
        @Path("articleId") articleId: Int,
        @Header("userId") userId: Int
    ): Response<Unit>
}
