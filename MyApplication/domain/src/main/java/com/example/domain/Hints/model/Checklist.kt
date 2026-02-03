package com.example.domain.Hints.model

//краткая инструкция в конце статьи (как краткий итог)
data class Checklist (
    val id: Long,
    val articleId: Long,
    val items: List<String>
)