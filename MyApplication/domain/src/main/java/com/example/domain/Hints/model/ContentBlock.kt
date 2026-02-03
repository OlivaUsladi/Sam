package com.example.domain.Hints.model

//контент статьи
sealed class ContentBlock {
    data class Paragraph(
        val text: String,
        val style: TextStyle = TextStyle.Normal
    ) : ContentBlock()

    data class Heading(
        val text: String,
        val level: Int = 2
    ) : ContentBlock()

    data class Image(
        val imageId: Int,
        val url: String,
        val width: Int? = null,
        val height: Int? = null
    ) : ContentBlock()
}

enum class TextStyle { Normal, Bold, Italic, Quote }