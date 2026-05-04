package com.example.data.Recipes.remote.mapper

import com.example.data.Recipes.remote.dto.*
import com.example.domain.Recipes.model.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object RecipeNetworkMapper {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun mapToDomain(dto: RecipeResponseDto): Recipe {
        return Recipe(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            categories = dto.categories.map { mapToDomain(it) },
            ingredients = emptyList(),
            author = dto.author,
            previewImageUrl = dto.previewImageUrl,
            cookingTimeMinutes = dto.cookingTimeMinutes,
            createdAt = LocalDateTime.parse(dto.createdAt, dateFormatter),
            updatedAt = LocalDateTime.parse(dto.updatedAt, dateFormatter),
            likesCount = dto.likesCount,
            isFavorite = dto.isFavorite,
            isLiked = dto.isLiked
        )
    }

    fun mapToDomain(dto: CategoryDto): Category {
        return Category(
            id = dto.id,
            name = dto.name,
            description = dto.description
        )
    }

    fun mapToDomain(dto: RecipeDetailResponseDto): Recipe {
        val ingredients = dto.ingredients.map { ingredientDto ->
            RecipeIngredient(
                groceryItem = GroceryItem(
                    id = ingredientDto.groceryItem.id,
                    groceryId = ingredientDto.groceryItem.groceryId,
                    name = ingredientDto.groceryItem.name,
                    defaultUnit = ingredientDto.groceryItem.defaultUnit
                ),
                amount = ingredientDto.amount.toDouble(),
                unit = ingredientDto.unit
            )
        }

        val cookingSteps = dto.cookingSteps.map { stepDto ->
            when (stepDto.type) {
                "paragraph" -> ContentBlock.Paragraph(
                    text = stepDto.text ?: "",
                    style = parseTextStyle(stepDto.style),
                    size = stepDto.size ?: 16,
                    area = parseTextArea(stepDto.area)
                )
                "image" -> ContentBlock.Image(
                    imageId = stepDto.imageId ?: 0,
                    url = stepDto.url ?: "",
                    width = stepDto.width,
                    height = stepDto.height
                )
                else -> ContentBlock.Paragraph(
                    text = "",
                    style = TextStyle.Normal,
                    size = 16,
                    area = TextArea.Left
                )
            }
        }

        return Recipe(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            categories = dto.categories.map { mapToDomain(it) },
            ingredients = ingredients,
            author = dto.author,
            previewImageUrl = dto.previewImageUrl,
            cookingTimeMinutes = dto.cookingTimeMinutes,
            createdAt = LocalDateTime.parse(dto.createdAt, dateFormatter),
            updatedAt = LocalDateTime.parse(dto.updatedAt, dateFormatter),
            likesCount = dto.likesCount,
            isFavorite = dto.isFavorite,
            isLiked = dto.isLiked
        )
    }

    private fun parseTextStyle(style: String?): TextStyle {
        return when (style?.lowercase()) {
            "bold" -> TextStyle.Bold
            "italic" -> TextStyle.Italic
            "underlined" -> TextStyle.Underlined
            else -> TextStyle.Normal
        }
    }

    private fun parseTextArea(area: String?): TextArea {
        return when (area?.lowercase()) {
            "center" -> TextArea.Center
            "right" -> TextArea.Right
            else -> TextArea.Left
        }
    }
}