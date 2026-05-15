package com.example.data.Hints.datasource.remote.mapper

import com.example.data.Hints.datasource.remote.dto.ArticleCategoryDto
import com.example.data.Hints.datasource.remote.dto.ArticleDetailResponseDto
import com.example.data.Hints.datasource.remote.dto.ArticleResponseDto
import com.example.data.Hints.datasource.remote.dto.ContentBlockDto
import com.example.domain.Hints.model.Article
import com.example.domain.Hints.model.ArticleContent
import com.example.domain.Hints.model.Category
import com.example.domain.Hints.model.ContentBlock
import com.example.domain.Hints.model.TextArea
import com.example.domain.Hints.model.TextStyle
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ArticleNetworkMapper {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun mapToDomain(dto: ArticleCategoryDto): Category {
        return Category(
            id = dto.id,
            name = dto.name,
            description = dto.description
        )
    }

    fun mapToDomain(dto: ArticleResponseDto): Article {
        return Article(
            id = dto.id,
            title = dto.title,
            category = mapToDomain(dto.category),
            mainWords = dto.mainWords ?: emptyList(),
            author = dto.author ?: "",
            imageUrl = dto.imageUrl,
            createdAt = parseDateOrNow(dto.createdAt),
            updatedAt = parseDateOrNow(dto.updatedAt),
            likesCount = dto.likesCount,
            isFavorite = dto.isFavorite,
            isLiked = dto.isLiked
        )
    }

    fun mapDetailToDomain(dto: ArticleDetailResponseDto): Article {
        return Article(
            id = dto.id,
            title = dto.title,
            category = mapToDomain(dto.category),
            mainWords = dto.mainWords ?: emptyList(),
            author = dto.author ?: "",
            imageUrl = dto.imageUrl,
            createdAt = parseDateOrNow(dto.createdAt),
            updatedAt = parseDateOrNow(dto.updatedAt),
            likesCount = dto.likesCount,
            isFavorite = dto.isFavorite,
            isLiked = dto.isLiked
        )
    }

    fun mapDetailToContent(dto: ArticleDetailResponseDto): ArticleContent {
        val blocks = (dto.blocks ?: emptyList()).map { mapBlock(it) }
        return ArticleContent(
            articleId = dto.id,
            blocks = blocks,
            checklist = dto.checklist ?: ""
        )
    }

    private fun mapBlock(dto: ContentBlockDto): ContentBlock {
        return when (dto.type) {
            "paragraph" -> ContentBlock.Paragraph(
                text = dto.text ?: "",
                style = parseTextStyle(dto.style),
                size = dto.size ?: 16,
                area = parseTextArea(dto.area)
            )
            "image" -> ContentBlock.Image(
                imageId = dto.imageId ?: 0,
                url = dto.url ?: "",
                width = dto.width,
                height = dto.height
            )
            else -> ContentBlock.Paragraph(
                text = "",
                style = TextStyle.Normal,
                size = 16,
                area = TextArea.Left
            )
        }
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

    private fun parseDateOrNow(value: String?): LocalDateTime {
        if (value.isNullOrBlank()) return LocalDateTime.now()
        return try {
            LocalDateTime.parse(value, dateFormatter)
        } catch (e: Exception) {
            LocalDateTime.now()
        }
    }
}
