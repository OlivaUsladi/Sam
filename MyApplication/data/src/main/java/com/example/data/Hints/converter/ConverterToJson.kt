package com.example.data.Hints.converter

import com.example.domain.Hints.model.ArticleContent
import com.example.domain.Hints.model.ContentBlock
import com.example.domain.Hints.model.TextArea
import com.example.domain.Hints.model.TextStyle
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.BufferedReader
import java.io.File

class ConverterToJson {

    private val json = Json {
        prettyPrint = false
        encodeDefaults = true
        classDiscriminator = "type"
        ignoreUnknownKeys = true
    }

    fun convertFromTXT(
        content: String
    ): List<ContentBlock>{

        val lines = content.lines().filter { it.isNotBlank() }
        val blocks = mutableListOf<ContentBlock>()

        lines.forEach { line ->
            when {
                line.startsWith("Image") -> blocks.add(parseImage(line))
                line.startsWith("Bold") || line.startsWith("Normal") ||
                        line.startsWith("Italic") || line.startsWith("Underlined") ->
                    blocks.add(parseText(line))
            }

        }
        return blocks
    }

    //Image, ID, URL, Width, Height
    private fun parseImage(line: String): ContentBlock.Image {
        val parts = line.split(",").map { it.trim() }

        val imageId = parts[1].toIntOrNull() ?: 0
        val url = parts[2]

        var width: Int? = 300
        var height: Int? = 300

        if (parts.size > 3) {
            if (parts[3]!="-"){
                width = parts[3].toIntOrNull()
            }

        }


        if (parts.size > 4) {
            if (parts[4]!="-"){
                height = parts[4].toIntOrNull()
            }
        }
        return ContentBlock.Image(imageId, url, width = width, height = height)
    }


    private fun parseText(line: String): ContentBlock.Paragraph {
        val colonIndex = line.indexOf(':')
        val metadata = line.substring(0, colonIndex).trim()
        val text = line.substring(colonIndex + 1).trim()

        val parts = metadata.split(",").map { it.trim() }

        val style = when (parts[0]) {
            "Bold" -> TextStyle.Bold
            "Italic" -> TextStyle.Italic
            "Underlined" -> TextStyle.Underlined
            else -> TextStyle.Normal
        }

        val size = parts[1].toIntOrNull() ?: 14

        val area = when (parts[2]) {
            "Center" -> TextArea.Center
            "Right" -> TextArea.Right
            else -> TextArea.Left
        }

        return ContentBlock.Paragraph(
            text = text,
            style = style,
            size = size,
            area = area
        )
    }

    fun convertBlocksToJson(blocks: List<ContentBlock>): String {
        return json.encodeToString(blocks)
    }

    fun convertJsonToBlocks(jsonString: String): List<ContentBlock> {
        return json.decodeFromString(jsonString)
    }

}