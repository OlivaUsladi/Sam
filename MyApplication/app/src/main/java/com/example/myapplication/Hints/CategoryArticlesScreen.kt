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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.input.rememberTextFieldState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.domain.Hints.model.Article
import com.example.domain.Hints.model.Category
import com.example.myapplication.R
import java.time.format.DateTimeFormatter

@Composable
fun CategoryArticlesScreen(
    navController: NavController,
    categoryId: Int,
    categoryName: String
) {
    val textState = rememberTextFieldState()

    val imageUrls = listOf(
        "https://i.ibb.co/bjy899VJ/aerial-view-business-data-analysis-graph.jpg",
        "https://i.ibb.co/zTjB1k12/brunette-woman-sitting-desk-surrounded-with-gadgets-papers.jpg",
        "https://i.ibb.co/274TSKSf/close-up-person-meditating-home.jpg",
        "https://i.ibb.co/Ld1K3vgj/tea-book-relax.jpg",
        "https://i.ibb.co/gMNnL2Yp/ceramic-mug-with-coffee-silver-dollar-gum-leaves.jpg",
        "https://i.ibb.co/YF85HrRg/doctor-doing-their-work-pediatrics-office.jpg"
    )

    val allArticles = remember {
        listOf(
            Article(
                id = 1,
                title = "Как управлять временем",
                category = Category(
                    id = 1,
                    name = "Продуктивность",
                    description = "Статьи об увеличении ритма жизни, чтобы больше успеть"
                ),
                mainWords = listOf("тайм-менеджмент", "планирование"),
                author = "Анна Смирнова",
                imageUrl = imageUrls[0],
                createdAt = java.time.LocalDateTime.now().minusDays(2),
                updatedAt = java.time.LocalDateTime.now().minusDays(1),
                likesCount = 124,
                isFavorite = false,
                isLiked = false
            ),
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
                createdAt = java.time.LocalDateTime.now().minusDays(5),
                updatedAt = java.time.LocalDateTime.now().minusDays(3),
                likesCount = 89,
                isFavorite = true,
                isLiked = true
            ),
            Article(
                id = 3,
                title = "Медитация для начинающих",
                category = Category(
                    id = 2,
                    name = "Здоровье",
                    description = "Статьи о здоровом образе жизни"
                ),
                mainWords = listOf("mindfulness", "релаксация"),
                author = "Елена Козлова",
                imageUrl = imageUrls[2],
                createdAt = java.time.LocalDateTime.now().minusWeeks(1),
                updatedAt = java.time.LocalDateTime.now().minusDays(2),
                likesCount = 256,
                isFavorite = false,
                isLiked = false
            ),
            Article(
                id = 4,
                title = "Здоровый сон и режим",
                category = Category(
                    id = 2,
                    name = "Здоровье",
                    description = "Статьи о здоровом образе жизни"
                ),
                mainWords = listOf("сон", "циркадные ритмы"),
                author = "Михаил Васильев",
                imageUrl = imageUrls[3],
                createdAt = java.time.LocalDateTime.now().minusDays(10),
                updatedAt = java.time.LocalDateTime.now().minusDays(8),
                likesCount = 67,
                isFavorite = false,
                isLiked = false
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
                createdAt = java.time.LocalDateTime.now().minusDays(3),
                updatedAt = java.time.LocalDateTime.now().minusDays(2),
                likesCount = 192,
                isFavorite = true,
                isLiked = false
            ),
            Article(
                id = 6,
                title = "Как прикрепиться в поликлинике",
                category = Category(
                    id = 4,
                    name = "Организация",
                    description = "Статьи о том, какие документы, куда, зачем предоставлять"
                ),
                mainWords = listOf("здоровье", "документы", "поликлиника", "больница"),
                author = "Александра Майснер",
                imageUrl = imageUrls[5],
                createdAt = java.time.LocalDateTime.now().minusDays(21),
                updatedAt = java.time.LocalDateTime.now().minusDays(13),
                likesCount = 888,
                isFavorite = false,
                isLiked = false
            ),
        )
    }

    // Фильтр статей по категории
    val categoryArticles = remember(categoryId) {
        allArticles.filter { it.category.id == categoryId }
    }

    // Список названий статей для поиска
    val searchTitles = remember { categoryArticles.map { it.title } }

    // Словари
    var favoriteStatus by remember { mutableStateOf(categoryArticles.associate { it.id to it.isFavorite }) }
    var likeStatus by remember { mutableStateOf(categoryArticles.associate { it.id to it.isLiked }) }

    var searchResults by remember { mutableStateOf(searchTitles) }
    var displayedArticles by remember { mutableStateOf(categoryArticles) }
    var showContent by remember { mutableStateOf(true) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }

    //Фильтрация статей (название или основные слова)
    fun filterSearchResults(query: String) {
        if (query.isBlank()) {
            displayedArticles = categoryArticles
        } else {
            displayedArticles = categoryArticles.filter { article ->
                article.title.contains(query, ignoreCase = true) ||
                        article.mainWords.any { it.contains(query, ignoreCase = true) }
            }
        }
        showContent = true
    }

    //Подсказки при вводе (название)
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

    //Постановка лайка
    fun toggleLike(articleId: Int) {
        likeStatus = likeStatus.toMutableMap().apply {
            put(articleId, !(likeStatus[articleId] ?: false))
        }
    }

    //Добавление в избранное
    fun toggleFavorite(articleId: Int) {
        favoriteStatus = favoriteStatus.toMutableMap().apply {
            put(articleId, !(favoriteStatus[articleId] ?: false))
        }
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
            Box(
                modifier = Modifier
                    .clickable {
                        navController.navigateUp()
                    }
            ) {
                Image(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = "Назад",
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = categoryName,
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
                CategorySearchBar(
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
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }

                items(items = displayedArticles, key = { it.id }) { article ->
                    ArticleCard(
                        article = article,
                        isFavorite = favoriteStatus[article.id] ?: false,
                        isLiked = likeStatus[article.id] ?: false,
                        onFavoriteClick = { toggleFavorite(article.id) },
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

@Composable
fun CategorySearchBar(
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by rememberSaveable { mutableStateOf("") }
    var showSuggestions by rememberSaveable { mutableStateOf(false) }

    val brush = remember {
        Brush.linearGradient(
            colors = listOf(Color(0xFF2670CC), Color(0xFF26CCAD))
        )
    }

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            TextField(
                value = query,
                onValueChange = { newValue ->
                    query = newValue
                    onQueryChange(newValue)
                    showSuggestions = newValue.isNotEmpty() && suggestions.isNotEmpty()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black),
                placeholder = {
                    Text(
                        text = "Поиск по категории",
                        style = TextStyle(brush = brush)
                    )
                },
                leadingIcon = {
                    IconButton(
                        onClick = {
                            onSearch(query)
                            showSuggestions = false
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Поиск",
                            tint = Color(0xFF2670CC)
                        )
                    }
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                query = ""
                                onQueryChange("")
                                onSearch("")
                                showSuggestions = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Очистить",
                                tint = Color(0xFF26CCAD)
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.White
                ),
                textStyle = TextStyle(brush = brush),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearch(query)
                        showSuggestions = false
                    }
                ),
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )
        }

        if (showSuggestions && suggestions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(brush, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    suggestions.forEachIndexed { index, suggestion ->
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = suggestion,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            },
                            modifier = Modifier
                                .clickable {
                                    query = suggestion
                                    onSuggestionClick(suggestion)
                                    showSuggestions = false
                                }
                                .fillMaxWidth(),
                            colors = ListItemDefaults.colors(
                                containerColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    }
}