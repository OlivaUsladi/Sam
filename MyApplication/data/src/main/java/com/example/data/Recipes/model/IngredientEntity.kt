package com.example.data.Recipes.model

import com.example.domain.Recipes.model.Ingredient

data class IngredientEntity(
    val id: Int,
    val name: String,
    val amount: Double,
    val unit: String,
    val recipeId: Int
) {
    fun toDomain(): Ingredient {
        return Ingredient(
            id = id,
            name = name,
            amount = amount,
            unit = unit
        )
    }

}