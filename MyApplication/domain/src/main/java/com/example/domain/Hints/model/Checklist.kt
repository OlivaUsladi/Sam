package com.example.domain.Hints.model

//краткая инструкция в конце статьи (как краткий итог)
data class Checklist (
    val id: Int,
    val articleId: Int,
    val items: List<String>
)