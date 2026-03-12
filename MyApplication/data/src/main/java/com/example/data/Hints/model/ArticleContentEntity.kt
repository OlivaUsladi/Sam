package com.example.data.Hints.model

import com.example.domain.Hints.model.ArticleContent
import com.example.domain.Hints.model.ContentBlock

data class ArticleContentEntity(
    val articleId: Int,
    val blocks: List<ContentBlock>,
    val checklistId: Int
) {
    fun toDomain(): ArticleContent {
        return ArticleContent(
            articleId = articleId,
            blocks = blocks,
            checklistId = checklistId
        )
    }

    companion object {
        fun fromDomain(content: ArticleContent): ArticleContentEntity {
            return ArticleContentEntity(
                articleId = content.articleId,
                blocks = content.blocks,
                checklistId = content.checklistId
            )
        }
    }
}