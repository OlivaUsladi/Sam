package com.example.domain.Recipes.model

data class Ingredient (
    val id: Int,
    val name: String,
    val unit: String //единица измерения (гр, шт, мл)
)