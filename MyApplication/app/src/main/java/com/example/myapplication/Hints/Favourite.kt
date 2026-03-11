package com.example.myapplication.Hints

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.domain.Hints.model.Article
import com.example.domain.Hints.model.Category
import com.example.myapplication.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun FavouriteArticlesScreen(navController: NavController) {
    val imageUrls = listOf(
        "https://i.ibb.co/bjy899VJ/aerial-view-business-data-analysis-graph.jpg",
        "https://i.ibb.co/zTjB1k12/brunette-woman-sitting-desk-surrounded-with-gadgets-papers.jpg",
        "https://i.ibb.co/274TSKSf/close-up-person-meditating-home.jpg",
        "https://i.ibb.co/Ld1K3vgj/tea-book-relax.jpg",
        "https://i.ibb.co/gMNnL2Yp/ceramic-mug-with-coffee-silver-dollar-gum-leaves.jpg",
        "https://i.ibb.co/YF85HrRg/doctor-doing-their-work-pediatrics-office.jpg"
    )


    val favouriteArticles = remember {
        listOf(
            Article(
                id = 2,
                title = "Техники продуктивности",
                category = Category(
                    id = 1,
                    name = "Продуктивность",
                    description = "Статьи об увеличении ритма жизни, чтобы больше успеть"
                ),
                mainWords = listOf("pomodoro", "фокус"),
                author = "Иван Петров",
                imageUrl = imageUrls[1],
                createdAt = LocalDateTime.now().minusDays(5),
                updatedAt = LocalDateTime.now().minusDays(3),
                likesCount = 89,
                isFavorite = true,
                isLiked = true
            ),
            Article(
                id = 5,
                title = "Утренние ритуалы",
                category = Category(
                    id = 3,
                    name = "Привычки",
                    description = "Статьи о формировании полезных привычек"
                ),
                mainWords = listOf("утро", "рутина"),
                author = "Ольга Новикова",
                imageUrl = imageUrls[4],
                createdAt = LocalDateTime.now().minusDays(3),
                updatedAt = LocalDateTime.now().minusDays(2),
                likesCount = 192,
                isFavorite = true,
                isLiked = false
            )
        )
    }

    // Список названий статей для поиска
    val searchTitles = remember { favouriteArticles.map { it.title } }

    // Словари для состояний
    var favoriteStatus by remember { mutableStateOf(favouriteArticles.associate { it.id to it.isFavorite }) }
    var likeStatus by remember { mutableStateOf(favouriteArticles.associate { it.id to it.isLiked }) }

    // Результаты поиска и отображаемые статьи
    var searchResults by remember { mutableStateOf(searchTitles) }
    var displayedArticles by remember { mutableStateOf(favouriteArticles) }
    var showContent by remember { mutableStateOf(true) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }

    // Функция поиска в поисковой строке (с выдачей карточек)
    fun filterSearchResults(query: String) {
        if (query.isBlank()) {
            displayedArticles = favouriteArticles
        } else {
            displayedArticles = favouriteArticles.filter { article ->
                article.title.contains(query, ignoreCase = true) ||
                        article.mainWords.any { it.contains(query, ignoreCase = true) }
            }
        }
        showContent = true
    }

    // Функция поиска в поисковой строке (с выдачей подсказок)
    fun getSuggestions(query: String): List<String> {
        if (query.isBlank()) {
            searchResults = searchTitles
        } else {
            searchResults = searchTitles.filter { title ->
                title.contains(query, ignoreCase = true)
            }
        }
        return searchResults
    }

    // Функция добавления лайка
    fun toggleLike(articleId: Int) {
        likeStatus = likeStatus.toMutableMap().apply {
            put(articleId, !(likeStatus[articleId] ?: false))
        }
    }

    // Функция удаления из избранного
    fun toggleFavorite(articleId: Int) {
        favoriteStatus = favoriteStatus.toMutableMap().apply {
            put(articleId, !(favoriteStatus[articleId] ?: false))
        }

        //Удаление из избранного
        //запрос к БД в будущем
        displayedArticles = displayedArticles.filter { it.id != articleId }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 10.dp)
        ) {
            Text(
                text = "Избранное",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically)
            )
        }

        LazyColumn {
            item {
                HintsSearchBar(
                    onQueryChange = { newQuery ->
                        getSuggestions(newQuery)
                        showContent = searchResults.isEmpty()
                    },
                    onSearch = { searchQuery ->
                        filterSearchResults(searchQuery)
                    },
                    suggestions = searchResults,
                    onSuggestionClick = { suggestion ->
                        filterSearchResults(suggestion)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (showContent) {
                if (displayedArticles.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.bookmark),
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "В избранном пока пусто",
                                    color = Color.Gray,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Добавляйте статьи в избранное,\nчтобы они появлялись здесь",
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    items(items = displayedArticles, key = { it.id }) { article ->
                        ArticleCard(
                            article = article,
                            isFavorite = favoriteStatus[article.id] ?: true,
                            isLiked = likeStatus[article.id] ?: false,
                            onFavoriteClick = {
                                toggleFavorite(article.id)
                            },
                            onLikeClick = { toggleLike(article.id) },
                            dateFormatter = dateFormatter,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate("article/${article.id}")
                                }
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                    }

                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }
    }
}

