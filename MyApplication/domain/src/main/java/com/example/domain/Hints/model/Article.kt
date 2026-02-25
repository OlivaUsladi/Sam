package com.example.domain.Hints.model

import com.example.domain.User
import java.time.LocalDateTime

//общая сущность статьи без контента
data class Article (
    val id: Int,
    val title: String,
    val category: Category,
    val mainWords: List<String>,
    val author: String,
    val imageUrl: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val likesCount: Int,
    val isFavorite: Boolean = false,
    val isLiked: Boolean = false
)