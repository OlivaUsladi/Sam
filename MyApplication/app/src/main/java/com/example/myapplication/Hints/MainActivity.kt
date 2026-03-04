package com.example.myapplication.Hints

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.data.Hints.converter.ConverterToJson
import com.example.domain.Hints.model.Article
import com.example.domain.Hints.model.Category
import com.example.domain.User
import com.example.myapplication.Hints.ui.theme.MyApplicationTheme
import com.example.myapplication.R
import java.io.File
import java.nio.file.WatchEvent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            Surface() {
                Scaffold (
                    topBar = { HintsTopAppBar(navController =
                        navController)},
                    bottomBar = {
                        /*BottomNavigationBar(navController =
                            navController)

                         */
                    },
                    content = {
                            paddingValues ->
                        Box(modifier = Modifier.padding(paddingValues)) {
                            HintsNavHost(navController = navController)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ListOfArticles(navController: NavController){

    val textState = rememberTextFieldState()

    val brush = remember {
        Brush.linearGradient(
            colors = listOf(Color(0xFF2670CC), Color(0xFF26CCAD))
        )
    }

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
                createdAt = LocalDateTime.now().minusDays(2),
                updatedAt = LocalDateTime.now().minusDays(1),
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
                createdAt = LocalDateTime.now().minusDays(5),
                updatedAt = LocalDateTime.now().minusDays(3),
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
                createdAt = LocalDateTime.now().minusWeeks(1),
                updatedAt = LocalDateTime.now().minusDays(2),
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
                createdAt = LocalDateTime.now().minusDays(10),
                updatedAt = LocalDateTime.now().minusDays(8),
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
                createdAt = LocalDateTime.now().minusDays(3),
                updatedAt = LocalDateTime.now().minusDays(2),
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
                createdAt = LocalDateTime.now().minusDays(21),
                updatedAt = LocalDateTime.now().minusDays(13),
                likesCount = 888,
                isFavorite = false,
                isLiked = false
            ),
        )
    }


    //список названий статей
    val searchTitles = remember { allArticles.map { it.title } }

    //словарь id-isFavoutite
    var favoriteStatus by remember { mutableStateOf(allArticles.associate { it.id to it.isFavorite }) }

    //список названий для поисковой строки
    var searchResults by remember { mutableStateOf(searchTitles) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }

    //отфильтрованные статьи
    var displayedArticles by remember { mutableStateOf(allArticles) }

    var showContent by remember { mutableStateOf(true) }


    //---------------------------------------------------------
    //Памятка: подсказки выводятся только по названию статьи, а поиск по mainwords через кнопку Enter
    // или лупу
    //---------------------------------------------------------

    //Функция поиска в поисковой строке (с выдачей карточек)
    fun filterSearchResults(query: String) {
        if (query.isBlank()) {
            displayedArticles = allArticles
        } else {
            displayedArticles = allArticles.filter { article -> article.title.contains(query, ignoreCase = true)
                    || article.mainWords.any {it.contains(query, ignoreCase = true)}
            }
        }
        showContent = true
    }

    //Функция поиска в поисковой строке (с выдачей подсказок-названий)
    fun getSuggestions(query: String): List<String> {
        if (query.isBlank()) {
            searchResults = searchTitles
        } else {
            searchResults = searchTitles.filter { article ->
                article.contains(query, ignoreCase = true)
            }
        }
         return searchResults
    }

    //Функция добавления в избранное
    fun toggleFavorite(articleId: Int) {
        //mutable словарь
        favoriteStatus = favoriteStatus.toMutableMap().apply {
            put(articleId, !(favoriteStatus[articleId] ?: false))
        }
    }


    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF000000))){

        LazyColumn() {
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
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(brush = brush)
                            //.heightIn(min = 100.dp)
                            .clickable(onClick = {
                                //Переход на страницу тем
                            })
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(7f)
                            ) {
                                Text(
                                    text = "Темы",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = "Выбирайте нужное, открывайте новое",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }

                            Image(
                                painter = painterResource(R.drawable.img_2),
                                contentDescription = "arrow",
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(50.dp))
                }

                items(items = displayedArticles, key = { it.id }) { article ->
                    ArticleCard(
                        article = article,
                        isFavorite = favoriteStatus[article.id] ?: false,
                        onFavoriteClick = { toggleFavorite(article.id) },
                        dateFormatter = dateFormatter,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val articleId = article.id
                                navController.navigate(Routes.Article.route + "/$articleId")
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
fun HintsSearchBar(
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
                        text = "Поиск",
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
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
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
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
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

@Composable
fun ArticleCard(
    article: Article,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    dateFormatter: DateTimeFormatter,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp)),
                //.background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            if (!article.imageUrl.isNullOrEmpty()) {
               AsyncImage(
                    model = article.imageUrl,
                    contentDescription = "Обложка для карточки",
                    error = painterResource(R.drawable.img_3),
                    placeholder = painterResource(R.drawable.loading),
                    modifier = Modifier.fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                   contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.img_3),
                    contentDescription = "No image",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = article.title,
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = article.createdAt.format(dateFormatter),
                color = Color.Gray,
                fontSize = 12.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        // Здесь постановка лайка
                    }
                ) {
                    Icon(
                        imageVector = if (article.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Лайки",
                        tint = if (article.isLiked) Color.Red else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = article.likesCount.toString(),
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "В избранное",
                        tint = if (isFavorite) Color.Red else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting() {
    //----------------------------------------------------------
    //Функция чтения файла и передачи в конвертер для преобразования текстового файла в json
    val context = LocalContext.current
    val converterToJson = ConverterToJson()

    LaunchedEffect(Unit) {
        val content = context.assets.open("Статья прикрепление к поликлинике.txt")
            .bufferedReader().use { it.readText() }

        val blocks = converterToJson.convertFromTXT(content)
        val json = converterToJson.convertBlocksToJson(blocks)
        println(json)
    }
    //-------------------------------------------------------------

    Text("work work")
}
