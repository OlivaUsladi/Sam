package com.example.domain.Recipes.model

//Основной продукт, из которого состоит блюдо: мясо, фрукты, крупы и т.д.
data class Grocery (
    val id: Int,
    val name: String,
    val description: String? = null
)