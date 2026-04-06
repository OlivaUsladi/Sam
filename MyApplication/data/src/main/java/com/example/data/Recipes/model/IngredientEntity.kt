package com.example.data.Recipes.model

import com.example.domain.Recipes.model.Ingredient

data class IngredientEntity(
    val id: Int,
    val name: String,
    val unit: String
) {
    fun toDomain(): Ingredient {
        return Ingredient(
            id = id,
            name = name,
            unit = unit
        )
    }

}