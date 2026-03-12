package com.example.myapplication.Hints.ui.categoryarticles

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myapplication.Hints.components.ArticleCard
import com.example.myapplication.Hints.components.HintsSearchBar
import com.example.myapplication.Hints.navigation.Routes
import com.example.myapplication.R
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CategoryArticlesScreen(
    navController: NavController,
    categoryId: Int,
    categoryName: String,
    viewModel: CategoryArticlesViewModel = koinViewModel(
        parameters = { parametersOf(categoryId, categoryName) }
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(categoryId) {
        viewModel.onEvent(CategoryArticlesEvent.LoadArticles(categoryId))
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
                    .clickable { navController.navigateUp() }
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

        HintsSearchBar(
            query = uiState.searchQuery,
            onQueryChange = { viewModel.onEvent(CategoryArticlesEvent.SearchQueryChanged(it)) },
            onSearch = { viewModel.onEvent(CategoryArticlesEvent.Search(it)) },
            suggestions = uiState.searchSuggestions,
            showSuggestions = uiState.showSuggestions,
            onSuggestionClick = { viewModel.onEvent(CategoryArticlesEvent.Search(it)) },
            onClearClick = { viewModel.onEvent(CategoryArticlesEvent.ClearSearch) },
            modifier = Modifier.fillMaxWidth()
        )

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
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                }
            }

            else -> {
                LazyColumn {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    items(
                        items = uiState.articles,
                        key = { it.id }
                    ) { article ->
                        ArticleCard(
                            article = article,
                            onFavoriteClick = {
                                viewModel.onEvent(CategoryArticlesEvent.ToggleFavorite(article.id))
                            },
                            onLikeClick = {
                                viewModel.onEvent(CategoryArticlesEvent.ToggleLike(article.id))
                            },
                            onArticleClick = {
                                navController.navigate("${Routes.Article.route}/${article.id}")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }
    }
}