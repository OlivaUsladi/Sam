package com.example.data.Hints.model

import com.example.data.Hints.model.CategoryEntity
import com.example.domain.Hints.model.Article
import java.time.LocalDateTime

data class ArticleEntity(
    val id: Int,
    val title: String,
    val categoryId: Int,
    val mainWords: List<String>,
    val author: String,
    val imageUrl: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    fun toDomain(category: CategoryEntity, likesCount: Int): Article {
        return Article(
            id = id,
            title = title,
            category = category.toDomain(),
            mainWords = mainWords,
            author = author,
            imageUrl = imageUrl,
            createdAt = createdAt,
            updatedAt = updatedAt,
            likesCount = likesCount
        )
    }
}