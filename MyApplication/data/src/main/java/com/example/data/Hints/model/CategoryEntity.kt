package com.example.data.Hints.model

import com.example.domain.Hints.model.Category

data class CategoryEntity(
    val id: Int,
    val name: String,
    val description: String?
) {
    fun toDomain(): Category {
        return Category(
            id = id,
            name = name,
            description = description
        )
    }
}