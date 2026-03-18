package com.example.domain.Recipes.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
sealed class ContentBlock {

    @Serializable
    @SerialName("paragraph")
    data class Paragraph(
        val text: String,
        val style: TextStyle = TextStyle.Normal,
        val size: Int,
        val area: TextArea = TextArea.Left
    ) : ContentBlock()

    @Serializable
    @SerialName("image")

    data class Image(
        val imageId: Int,
        val url: String,
        val width: Int? = null,
        val height: Int? = null
    ) : ContentBlock()
}



    @Serializable
    enum class TextStyle {
        @SerialName("normal") Normal,
        @SerialName("bold") Bold,
        @SerialName("italic") Italic,
        @SerialName("underlined") Underlined
    }

    @Serializable
    enum class TextArea {
        @SerialName("right") Right,
        @SerialName("center") Center,
        @SerialName("left") Left
    }