package com.example.data.Recipes.model

import com.example.domain.Recipes.model.Category

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