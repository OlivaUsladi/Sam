package com.example.myapplication.Hints.ui.article

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.domain.Hints.model.ContentBlock
import com.example.domain.Hints.model.TextArea
import com.example.domain.Hints.model.TextStyle
import com.example.myapplication.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun ArticleScreen(
    articleId: Int,
    viewModel: ArticleViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(articleId) {
        viewModel.onEvent(ArticleEvent.LoadArticle(articleId))
    }

    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        uiState.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ошибка: ${uiState.error}",
                    fontSize = 18.sp,
                    color = Color.Red
                )
            }
        }

        uiState.articleContent == null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Статья не найдена",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 5.dp, end = 5.dp)
                            ) {
                                Text(
                                    text = uiState.articleCategory,
                                    fontSize = 16.sp,
                                    color = Color.Gray,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(0.5f)
                                )
                                Text(
                                    text = uiState.articleDate,
                                    fontSize = 16.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.align(Alignment.Bottom)
                                )
                            }
                            AsyncImage(
                                model = uiState.articleImageUrl,
                                contentDescription = "Картинка для верха статьи",
                                error = painterResource(R.drawable.img_3),
                                placeholder = painterResource(R.drawable.loading),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                items(uiState.articleContent!!.blocks) { block ->
                    when (block) {
                        is ContentBlock.Paragraph -> {
                            ParagraphBlock(block)
                        }
                        is ContentBlock.Image -> {
                            ImageBlock(block)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun ParagraphBlock(paragraph: ContentBlock.Paragraph) {
    val textAlign = when (paragraph.area) {
        TextArea.Left -> TextAlign.Left
        TextArea.Center -> TextAlign.Center
        TextArea.Right -> TextAlign.Right
    }

    val fontWeight = when (paragraph.style) {
        TextStyle.Normal -> FontWeight.Normal
        TextStyle.Bold -> FontWeight.Bold
        TextStyle.Italic -> FontWeight.Normal
        TextStyle.Underlined -> FontWeight.Normal
    }

    val fontStyle = when (paragraph.style) {
        TextStyle.Italic -> androidx.compose.ui.text.font.FontStyle.Italic
        else -> androidx.compose.ui.text.font.FontStyle.Normal
    }

    val textDecoration = when (paragraph.style) {
        TextStyle.Underlined -> androidx.compose.ui.text.style.TextDecoration.Underline
        else -> androidx.compose.ui.text.style.TextDecoration.None
    }

    val topPadding = when {
        paragraph.style == TextStyle.Bold && paragraph.size == 24 -> 16.dp
        paragraph.style == TextStyle.Bold -> 24.dp
        else -> 8.dp
    }

    val bottomPadding = when {
        paragraph.style == TextStyle.Bold && paragraph.size == 24 -> 8.dp
        else -> 4.dp
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding, bottom = bottomPadding)
    ) {
        Text(
            text = paragraph.text,
            fontSize = paragraph.size.sp,
            fontWeight = fontWeight,
            fontStyle = fontStyle,
            textDecoration = textDecoration,
            textAlign = textAlign,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ImageBlock(image: ContentBlock.Image) {
    val width = image.width ?: 300
    val height = image.height ?: 300

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = image.url,
            contentDescription = "Изображение к статье",
            error = painterResource(R.drawable.img_3),
            placeholder = painterResource(R.drawable.loading),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(width.toFloat() / height.toFloat())
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Fit
        )
    }
}