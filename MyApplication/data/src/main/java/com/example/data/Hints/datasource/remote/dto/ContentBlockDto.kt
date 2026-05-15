package com.example.data.Hints.datasource.remote.dto

data class ContentBlockDto(
    val type: String,        // "paragraph" или "image"
    val text: String? = null,
    val style: String? = null,  // "normal" "bold" "italic" "underlined"
    val size: Int? = null,
    val area: String? = null,   // "left" "center" "right"
    val imageId: Int? = null,
    val url: String? = null,
    val width: Int? = null,
    val height: Int? = null
)
