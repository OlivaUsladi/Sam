package com.example.domain.Hints.model


//статья в развёрнутом виде

data class ArticleContent(
    val articleId: Int,
    val blocks: List<ContentBlock>,
    val checklist: String
)