package com.example.data.Recipes.model

import com.example.domain.Recipes.model.Grocery

data class GroceryEntity(
    val id: Int,
    val name: String,
    val description: String?
) {
    fun toDomain(): Grocery {
        return Grocery(
            id = id,
            name = name,
            description = description
        )
    }

}