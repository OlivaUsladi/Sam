package com.example.data.Hints.datasource.remote.dto

data class ArticleResponseDto(
    val id: Int,
    val title: String,
    val category: ArticleCategoryDto,
    val mainWords: List<String>?,
    val author: String?,
    val imageUrl: String?,
    val createdAt: String,
    val updatedAt: String,
    val likesCount: Int,
    val isFavorite: Boolean,
    val isLiked: Boolean
)
