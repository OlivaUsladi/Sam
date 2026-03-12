package com.example.data.Hints.model

import com.example.domain.Hints.model.Checklist

data class ChecklistEntity(
    val id: Int,
    val articleId: Int,
    val items: List<String>
) {
    fun toDomain(): Checklist {
        return Checklist(
            id = id,
            articleId = articleId,
            items = items
        )
    }

    companion object {
        fun fromDomain(checklist: Checklist): ChecklistEntity {
            return ChecklistEntity(
                id = checklist.id,
                articleId = checklist.articleId,
                items = checklist.items
            )
        }
    }
}
